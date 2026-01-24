# Firebase Database Setup Instructions

## Problem Summary

Your app's notification system is failing because Firebase Realtime Database is missing required indexes. The errors in your Logcat show:

```
Index not defined, add ".indexOn": "receivedAt", for path "/userNotifications/{userId}"
Index not defined, add ".indexOn": "isRead", for path "/userNotifications/{userId}"
Index not defined, add ".indexOn": "timestamp", for path "/users/{userId}/listeningHistory"
Index not defined, add ".indexOn": "uploadedBy", for path "/songs"
```

## Solution: Update Firebase Database Rules

### Step 1: Access Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **MusicPlayerApplication**
3. In the left sidebar, click on **Realtime Database**
4. Click on the **Rules** tab

### Step 2: Update Database Rules

**Option A: Copy from File (Recommended)**

1. Open the file `firebase-database-rules.json` in this directory
2. Copy the entire JSON content
3. Paste it into the Firebase Console Rules editor
4. Click **Publish**

**Option B: Manual Update**

Replace your current rules with the following:

```json
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "auth != null",
        ".write": "auth != null && auth.uid == $userId",
        "listeningHistory": {
          ".indexOn": ["timestamp"]
        },
        "profile": {
          ".read": "auth != null",
          ".write": "auth != null && auth.uid == $userId"
        }
      }
    },
    "songs": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["uploadedBy", "timestamp"]
    },
    "notifications": {
      "$notificationId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "userNotifications": {
      "$userId": {
        ".read": "auth != null && auth.uid == $userId",
        ".write": "auth != null",
        ".indexOn": ["receivedAt", "isRead"]
      }
    },
    "playlists": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

### Step 3: Publish the Rules

1. After pasting the rules, click the **Publish** button in the top right
2. Wait for confirmation that the rules have been published

### Step 4: Test the Notification System

1. **Restart your app** (close and reopen)
2. **Upload a new song** from your app
3. **Check the NotificationScreen** to see if the notification appears
4. **Check Logcat** - the index errors should no longer appear

## What These Indexes Do

### 1. `userNotifications/{userId}` - Indexes on "receivedAt" and "isRead"

**Purpose**: Allows efficient querying of notifications by time and read status

**Used in**: `NotificationRepositoryImpl.kt:110-112`
```kotlin
userNotificationsRef.child(userId)
    .orderByChild("receivedAt")
    .limitToLast(limit)
```

**And**: `NotificationRepositoryImpl.kt:232-235`
```kotlin
userNotificationsRef.child(userId)
    .orderByChild("isRead")
    .equalTo(false)
```

### 2. `users/{userId}/listeningHistory` - Index on "timestamp"

**Purpose**: Enables sorting listening history by timestamp

**Benefits**: Faster queries when displaying user's listening history chronologically

### 3. `songs` - Indexes on "uploadedBy" and "timestamp"

**Purpose**: Allows filtering songs by uploader and sorting by upload time

**Benefits**: Quick retrieval of songs uploaded by specific users

## Database Structure

Your app uses the following Firebase database structure:

```
/users
  /{userId}
    /profile          - User profile data
    /listeningHistory - Songs the user has listened to
      - timestamp: indexed for sorting

/songs
  /{songId}          - Song data
    - uploadedBy: indexed for filtering
    - timestamp: indexed for sorting

/notifications
  /{notificationId}  - Global notification data
    - title, message, songId, etc.

/userNotifications
  /{userId}
    /{notificationId}
      - notificationId: reference to /notifications
      - isRead: indexed for filtering
      - receivedAt: indexed for sorting
```

## Security Rules Explained

### User Data Protection
```json
"users": {
  "$userId": {
    ".read": "auth != null",
    ".write": "auth != null && auth.uid == $userId"
  }
}
```
- Any authenticated user can **read** user profiles
- Only the user themselves can **write** to their own data

### Notification Privacy
```json
"userNotifications": {
  "$userId": {
    ".read": "auth != null && auth.uid == $userId",
    ".write": "auth != null"
  }
}
```
- Users can only **read** their own notifications
- Any authenticated user can **write** notifications (needed for sending notifications to others)

### Songs Access
```json
"songs": {
  ".read": "auth != null",
  ".write": "auth != null"
}
```
- All authenticated users can read and upload songs

## Troubleshooting

### Issue: Notifications still not appearing

**Check 1**: Verify rules are published
- Go to Firebase Console → Realtime Database → Rules
- Ensure the rules show the indexes

**Check 2**: Check Logcat for errors
```bash
adb logcat -s NotificationRepo:* MusicViewModel:*
```

**Check 3**: Verify user authentication
- Ensure you're logged in when uploading a song
- Check Firebase Console → Authentication → Users

**Check 4**: Check database data
- Go to Firebase Console → Realtime Database → Data
- Navigate to `/notifications` and `/userNotifications/{your-userId}`
- Verify notifications are being created

### Issue: "Permission denied" errors

**Solution**: Ensure you're logged in
- The rules require `auth != null` for all operations
- Check `FirebaseAuth.getInstance().currentUser` is not null

### Issue: Old notifications still not loading

**Solution**:
1. The indexes only apply to new queries
2. Try creating a new notification by uploading a new song
3. If old notifications still don't load, they may need to be re-indexed by Firebase (this happens automatically)

## Additional Optimizations (Optional)

### Add Timestamp Index to Notifications

If you want to query global notifications by timestamp:

```json
"notifications": {
  ".indexOn": ["timestamp"],
  "$notificationId": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

### Add Composite Indexes (Advanced)

For complex queries combining multiple fields, you may need composite indexes. Contact Firebase support or refer to [Firebase documentation](https://firebase.google.com/docs/database/security/indexing-data) for more details.

## Verification Checklist

- [ ] Firebase Database Rules updated in console
- [ ] Rules published successfully
- [ ] App restarted
- [ ] New song uploaded
- [ ] Notification appears in NotificationScreen
- [ ] No index errors in Logcat
- [ ] Unread count shows correctly
- [ ] Mark as read functionality works
- [ ] Delete notification functionality works

## Need Help?

If you encounter any issues:

1. **Check Firebase Console Logs**: Firebase Console → Realtime Database → Usage
2. **Check App Logs**: Run `adb logcat | grep -E "(NotificationRepo|MusicViewModel|FirebaseDatabase)"`
3. **Verify Firebase Configuration**: Ensure `google-services.json` is up to date

## References

- [Firebase Database Indexing](https://firebase.google.com/docs/database/security/indexing-data)
- [Firebase Security Rules](https://firebase.google.com/docs/database/security)
- [Firebase Realtime Database Best Practices](https://firebase.google.com/docs/database/usage/best-practices)
