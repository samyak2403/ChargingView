A simple customizable charging view for Android.

Usage
Step 1: Add JitPack Repository
Add the JitPack repository to your project's build.gradle file at the end of repositories:


allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}

Step 2: Add the Dependency
Add the dependency to your app's build.gradle file:

dependencies {
    implementation 'com.github.samlss:ChargingView:1.1'
}

Step 3: Add ChargingView to Your Layout
Add ChargingView to your layout.xml file:
<com.iigo.library.ChargingView
    android:id="@+id/cv2"
    android:layout_marginTop="10dp"
    android:layout_width="100dp"
    android:layout_height="200dp"
    app:progress="50"
    app:progressTextColor="@android:color/black"
    app:chargingColor="@android:color/holo_red_light"
    app:bg_color="#eeeeee"
    app:progressTextSize="20dp" />

Step 4: Use ChargingView in Your Java Code
Set properties programmatically in your Java code:

ChargingView chargingView = findViewById(R.id.cv2);
chargingView.setProgress(95);
chargingView.setBgColor(Color.parseColor("#aaaaaa"));
chargingView.setChargingColor(Color.YELLOW);
chargingView.setTextColor(Color.RED);
chargingView.setTextSize(25);


Attributes Description
Attribute	Description
bg_color	The background color
chargingColor	The charging color
progress	Current progress (0-100)
progressTextSize	The progress text size
progressTextColor	The progress text color
