# Treaty-DailyTask
I program everyday until i finish the project

## What to build
### Problem
When tracking money spending on certain categories user needs to input amount and description manually via notes and after collectively inserting all the amount and description at the end of the week user summarizes all the spending in a google sheet document wherein it computes the total spent plus the predicitve saving for the current month.

### Solution
Build a mobile application that supports Android and iOS which collects all the inputs of the amount and description/category of the user and automatically summarize all the spending and savings predicition for the current month.

## Where to build
### Platforms
- **Android** (Android Studio)<sup>Kotlin</sup>
- **iOS** (XCode)<sup>Swift</sup>
- **Web** *future projects most likely a cross-platform build*

### API Service & Database
- MongoDB Realm
- Google Sheets API (*Needs more R&D*)

## System Architecture *HIGH LEVEL DESIGN*
- Can do CRUD operation on category **price**, **description**
- Separated by Category UID
- Debounce offline save *background thread*
- Sync Save operation *background service task*
- Sync Retrieve operation *can be triggered manually or via app launch*
- Local Push Notification on not completed task **time based operation**

## Planning & System Design
1. Story planning
    1. Features
        - Create Task
        - Retrieve Task
        - Update Task
        - Delete Task
        - Complete Task
        - Debounce save operation *cache strategy*
        - Local Push Notification *time base reminder*
        - Sync Save *Service API*
        - Sync Retrieve *Service API*
    2. Edge cases (Error handling)
       - *INSERT DIAGRAM*
       
2. Development Architecture (Code) Base design
    1. Koin Dependency
    2. Realm Dependency
    3. Local Notification Dependency
    4. Testing Library Dependency
       
3. Layout & Design
4. CI/CD Deployment Status
