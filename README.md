## [中文介绍](https://blog.csdn.net/a296777513/article/details/70495534)

# Multiple Stream GLSufaceView

[ ![Download](https://api.bintray.com/packages/knight/maven/glview/images/download.svg?version=1.0.0) ](https://bintray.com/knight/maven/glview/1.0.0/link)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

This is a Library that can play multiple's video(resource can from video , camera and stream that from net) in a same GLSurfaceView, this function is just like **WeChat's** video call.

## Check The Result

![](https://github.com/296777513/Picture/blob/master/GLSurfaceView/AlphaPlayer.gif?raw=true)

From this gif, this is a GLSurfaceView render two video , this function simulate video call.

## Setup

In your module build.gradle:

```gradle
dependencies{
    implementation 'com.knight:glview:1.0.0'
}
```

## Usage

In layout's xml:

```xml
<com.knight.glview.CameraGLSurfaceView
        android:id="@+id/mCameraGLSurfaceView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

then you can run this project and see the effect. I have package all code in library, if you want konw this funtion's theroy, you can **download** source code , **reading the fxxking code **.

I just input a random video, and preview camera, you can realize more function.



# Improve

If you have some problem or advice, please don't hesitate to raise an issue. Just have fun and hope this can help you.

# License 

> Apache Version 2.0
>
> Copyright 2019 Knight
>
> Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
> 
> http://www.apache.org/licenses/LICENSE-2.0
> 
> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License





