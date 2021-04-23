## NEWSS
Newss is an android application mainly built for personal app development learning purpose. This application is built purely in java and is mainly for android platform. This app fetches the articles by consuming **[News API](https://newsapi.org/)**. It makes use of **[Retrofit](https://square.github.io/retrofit/)** type safe client for handling HTTP request along with **GSON** object converter. It uses **[Glide](https://bumptech.github.io/glide/)** for image downloading and caching. We can definitely use **[Picasso library](https://square.github.io/picasso/)** too. Please refer **[this](https://medium.com/@singhsiva177/difference-between-picasso-and-glide-5d4b944c7088#:~:text=Picasso%20downloads%20the%20image%20and,other%20hand%20glide%20work%20diffrently.&text=That%20will%20increase%20the%20disk%20image%20size.)** article for significant differences between both. We will also make use of another [third-party library](https://github.com/ocpsoft/prettytime) to beautify the timestamp of the news application.  

### 1. Including Third party Libraries:
Include the below implementations to your dependencies block inside `app.gradle` file.
```groovy
// Recycler view and card view dependency
implementation 'com.google.android.material:material:1.0.0'
implementation "androidx.cardview:cardview:1.0.0"
implementation "androidx.recyclerview:recyclerview:1.1.0"
// For control over item selection of both touch and mouse driven selection
implementation "androidx.recyclerview:recyclerview-selection:1.1.0"

// Image download and caching
implementation 'com.github.bumptech.glide:glide:4.11.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'

// Establishing HTTP connection with the API endpoints
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
implementation("com.squareup.okhttp3:okhttp:4.9.0")
// Time formatter
implementation 'org.ocpsoft.prettytime:prettytime:4.0.4.Final'
```
**NOTE: Please try to update to the libraries to their latest version. When I built this application these were the latest versions.**

### 2. Getting the API key from the News API
Head over to [News API](https://newsapi.org/) and create an account to get the API key (generally alphanumeric) which is very much required to establish the connection from the API endpoints.

### 3. Adding the permissions to manifest file
In your `AndroidManifest.xml` add the below permissions:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
    <!--
    Allows Glide to monitor connectivity status and restart failed requests if users go from a
    a disconnected to a connected network state.
    -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### 4. Tinkering with layouts and colors
   #### 4.1. 
