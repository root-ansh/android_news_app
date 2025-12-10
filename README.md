# CuriousNews
![app/src/main/ic_launcher-playstore.png](app_icon)


## Project Configuration
- Android Studio : [Android Studio Otter 3 Feature Drop | 2025.2.3 Canary 1](media/img.png)
- MVVM Clean Architecture With Usecase, Repo, Viewmodel, State, Hilt, Flows, Room Db , Nav3, Retrofit And Jetpack Compose

## Project Resources:
- video : 
- apk : 
- screenshots: 

## Checklist

### News Feed

- ✅ Show list of articles fetched from a public API (e.g., NewsAPI.org, GNews, or custom JSON). ✅ Used News API
  - ⚠️ The API only allows 100 api queries every 24 hrs. so if you are running the app, it would be better to add a fresh key in `gradle.properties` because i might have consumed the 100 requests during testing
- ✅ Each row/card shows: title, image, source, published date, short description (if available) ✅ Done
- ✅ Show loading states, error states, and empty-state UI:  ✅ Shimmer based loading, and simple empty/error states for headlines,search
- ✅ Support pagination — implement using Paging 3 (infinite scroll) or manual : ⏳ Manual Pagination

### Article Detail
- ✅ Tapping an article opens detailed screen
- ✅ Option A: native detail with formatted content (if API returns full content) : 🔵 Api just returns small details content, so rendered accordingly as "summary"
- ✅ Option B: WebView to open original URL.✅ Done
- ✅ Provide share (Android share sheet), bookmark, and “Open in browser” actions ✅ Done

### Article Detail
- ✅Search articles by keyword ✅
- ✅Implement debounced search input using Kotlin Flow (e.g., debounce)✅
- ✅Show search-specific loading/error/empty UI. ✅

### Bookmarks
- ✅Save/unsave articles locally. ✅
- ✅ Bookmarks accessible in a dedicated tab or bottom navigation item.✅
- ✅If an article is bookmarked, reflect state in the feed and detail screens. ✅ Reflecting in feed, detail and search

### Persistence
- ✅ Use Room to persist bookmarked articles (and optionally cached articles for  offline). ✅Caching logic :
  - Headlines screen UI is driven by room db cache. Headline api request response gets stored immediately in cache
  - For init screen, data is either shown from cache(if available else api). this is done to handle offline case, but comes with a tradeoff that user needs to scroll to get latest results
  - As user scrolls, a paginated api call is made . a loader is shown in the bottom
  -  ⚠️️⏳ for search results screen, no pagination was supported due to time constraints, but architecture exists
- ✅ Use Room to persist bookmarked articles (and optionally cached articles for offline) ✅ sample migration added, schemas getting exported to `/schemas` directory

### Tech Features 
- ✅ Clean Architecture or MVVM + UseCases. ✅
- ✅ Layers: presentation (Compose/ViewModels), domain (use-cases, models), data (Retrofit, Room, Paging). ✅
- ✅ Use Kotlin, coroutines, and Flow (no blocking calls on main thread)✅
- ✅ Dependency Injection: Hilt (preferred) or Dagger2. ✅
- ✅ Use Retrofit + OkHttp for HTTP calls. ✅
- ✅ Image loading: Coil (Compose-friendly).✅
- ✅ Network  map network errors to UI states.
- ✅ Use Kotlin coroutines + Flow for async work and state streams. ✅ done
- ✅ ViewModel exposes StateFlow or LiveData for UI. ✅done
- ✅ Use Paging 3 (recommended) with PagingSource and RemoteMediator if you add local caching ⏳ Manual Pagination
- ✅ Prefer Jetpack Compose✅ done
- ✅ Dark mode ✅ done
- ✅ Font Scaling ✅ ⏳architecture exists but didnt add
- ✅ Smooth transitions, placeholder images, shimmer or progress indicators,✅ done
- ✅ Use Navigation Component ✅ used compose nav 3
- ⚠️⏳ Handle failures: retry/backoff (OkHttp interceptors or wrapper)
- ⚠️⏳ Use WorkManager for scheduled background sync and local notifications (bonus feature)
- ⚠️⏳ Deep Linking : implement deep links opening specific articles.
- ⚠️⏳Unit tests for:Networking layer (use MockWebServer or mocked Retrofit), Repositories , UseCases, ViewModels (Test StateFlow/LiveData outputs)
- ⚠️⏳Instrumentation / UI tests: for Compose UI tests or Espresso for XML.
- ⚠️⏳Integration tests :  navigation flows.




