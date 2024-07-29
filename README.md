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

## How to build
- Can do CRUD operation on category **price**, **description**
- Separated by UID
- Debounce offline save *background thread*
- Sync Save operation *background service task*
- Sync Retrieve operation *can be triggered manually or via app launch*

## Planning & System Design
1. Story planning
    1. Features
    2. Use cases
    3. Edge cases (Error handling)
2. System Architecture (High level design)
3. Development Architecture (Code) Base design
4. Layout & Design
5. CI/CD Deployment Status
