# TakeHome[Beta]

## 1.Brief:

It is coding for Take-Home-Test, and you can install the "app-debug.apk" on the Android(8.0+ Oreo) smartphone.

DEMO Video Link: [video](https://drive.google.com/open?id=1-KYzfU4o94Yc4AklZVu6MlcEun_hl8UA)

## 2.Instructions for use:

First, users should import the data by clicking "ADD DATA!" button, before searching. [Notice] this button only needs to be clicked once!

Second, after done First Step, users can input the origin and destination by IATA code, and click "SEARCH" button. Then, the following screen will show the results.

## 3.Main code part:

TakeHome/app/src/main/java/com/tianfei/takehometest/MainActivity.java TakeHome/app/src/main/java/com/tianfei/takehometest/MapActivity.java TakeHome/app/src/main/java/com/tianfei/takehometest/MyMatabaseHelper.java TakeHome/app/src/main/res/layout/activity_main.xml

## Update:

Adding Google-maps on the app, which means users can check the positions of the airports and routes on the map.
 METHOD: users click the list item of the result.

Update_20180916:

optimizaing UI with new look and animation, new Google Maps style.

Update_20181010:

Refactoring code by MVP pattern.
Main code part:
TakeHome/app/src/main/java/com/tianfei/takehometest/MainActivity.java TakeHome/app/src/main/java/com/tianfei/takehometest/MapActivity.java TakeHome/app/src/main/java/com/tianfei/takehometest/model/ TakeHome/app/src/main/res/layout/presenter/ TakeHome/app/src/main/java/com/tianfei/takehometest/view/<br>

New Look:
<img src="https://github.com/FrankTian0906/TakeHome/raw/master/image_for_demo/Screenshot_20181022-170636.png" width="300"> 