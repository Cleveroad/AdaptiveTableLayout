# TableLayout [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="19"></a>
![Header image](/images/header.jpg)

## Welcome the new CSV Library TableLayout for Android by Cleveroad

Pay your attention to our new library that makes it possible to read, edit and write CSV files. If you use Android-based device you can easily use our library for implementation of all aforementioned actions. 

![Demo image](/images/demo.gif)

For using this library in a valuable way, you can find our CSV Editor app on the Google Play Store. Enjoy it!

[![Awesome](/images/logo-footer.png)](https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts)
<br/>
## Setup and usage
### Installation
by gradle : 
```groovy
dependencies {
    compile "com.cleveroad:loopbar:1.1.3"
}
```

or just download zip and import module "LoopBar-widget" to be able to modify the sources

### Features
View consist from two parts:
 - A list of your selectable groups
 - Selected view

View can work in three scroll modes: infinite, finite and auto mode.
Names of first two speak for itself. In auto mode the list of groups
will be infinite if all adapter items didn't fit on screen in other case it will be a static list.
Selected view by request could overlay layout on screen on which it placed. 
Widget has horizontal and vertical layouts and also start or end gravity of selected view. 
<p>You are allowed to use any RecyclerView adapter, which you want. Concrete infinite scroll logic is fully encapsulated</p>

Android Studio layouts preview is supported.

### Usage
```XML
    <com.cleveroad.loopbar.widget.LoopBarView
        android:id="@+id/endlessView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:enls_placeholderId="@id/placeHolder"
        app:enls_orientation="horizontal"
        app:enls_selectionGravity="start"
        app:enls_selectionMargin="10dp"
        app:enls_overlaySize="5dp"
        app:enls_selectionInAnimation="@animator/enls_scale_restore"
        app:enls_selectionOutAnimation="@animator/enls_scale_small"
        app:enls_selectionBackground="@android:color/holo_blue_dark"
        app:enls_menu="@menu/loopbar"
        app:enls_scrollMode="auto"
        />
```

```enls_overlaySize``` & ```enls_placeholderId``` are used for overlay feature.
Placeholder must be the view lying under the LoopBar. And it's used for setting bounds In layout when selected view has overlay.
LoopBar will set width or height for this view by itself (depending of LoopBar orientation), it will be the same as LoopBar has.
Without this placeholder the bounds of LoopBar will be limited by selected view (with overlay). And thanks to placeholder the bounds
will be limited only by list of groups. You can see an example of overlay usage in sample.

|  attribute name | description |
|---|---|
| enls_overlaySize  | a size of selected view overlaying |
| enls_placeholderId | an id of view on which you should use layout:above or other attributes of RelativeLayouts,  because LoopBarView will have increased height in this case. See more in sample |
| enls_selectionGravity | a gravity of selection view. Can be vertical or horizontal. Default horizontal |
| enls_selectionMargin | a margin of selectionView from bounds of view. Default ```5dp``` |
| enls_selectionInAnimation | an animation of appearing an icon inside selection view |
| enls_selectionOutAnimation | an animation of hiding an icon inside selection view |
| enls_selectionBackground | selection background. Default ```#ff0099cc``` |
| enls_menu | an id of menu which will be used for getting title and icon items  |
| enls_scrollMode | Scrolling mode. Can be ```infinite```, ```finite```, ```auto```. Default ```infinite``` |
| android:background | View have yellow background by default. Use standart ```android:background``` attribute to change it. Default ```#ffc829``` |


To initialize items in widget and work with it you should setup adapter to it and add item click mListener:
```
LoopBarView loopBarView = findViewById(...);
categoriesAdapter = new SimpleCategoriesAdapter(MockedItemsFactory.getCategoryItems(getContext()));
loopBarView.setCategoriesAdapter(categoriesAdapter);
loopBarView.addOnItemClickListener(this);
```
Here SimpleCategoriesAdapter is used which required collection of [ICategoryItem] objects (to draw default view with icon and text).
<br /> Also you can setup adapter through:
*   **Menu** via Java code (see example [MenuLoopBarFragment]):
``` 
        loopBarView.setCategoriesAdapterFromMenu(R.menu.loopbar);
        //or
        Menu menu = ...;
        loopBarView.setCategoriesAdapterFromMenu(menu);
```
    or via XML:
```
        <com.cleveroad.loopbar.widget.LoopBarView
        ...
        app:enls_menu="@menu/loopbar"
        />
```
* **ViewPager**. Just set a viewPager into your LoopBar. If you want to show category icons, your ViewPager adapter must implement [ILoopBarPagerAdapter] interface (see example [ViewPagerLoopBarFragment]), otherwise the icons will not be shown:
```
    loopBarView.setupWithViewPager(viewPager);
```

To customize wrapped ```RecyclerView``` (control animations, decorators or add ```RecyclerView.OnScrollListener```) you are able to use following methods:

|  method name | description |
|---|---|
| ```setItemAnimator(RecyclerView.ItemAnimator animator)```  | Sets the ```ItemAnimator``` to wrapped RecyclerView |
| ```isAnimating()```  | Returns true if wrapped RecyclerView is currently running some animations |
| ```addItemDecoration(RecyclerView.ItemDecoration decor)```   | Add an ```ItemDecoration``` to wrapped RecyclerView |
| ```addItemDecoration(RecyclerView.ItemDecoration decor, int index)```  | Add an ```ItemDecoration``` to wrapped RecyclerView |
| ```removeItemDecoration(RecyclerView.ItemDecoration decor)``` | Remove an ```ItemDecoration``` from wrapped RecyclerView |
| ```invalidateItemDecorations()``` | Invalidates all ```ItemDecoration```s in wrapped RecyclerView |
| ```addOnScrollListener(RecyclerView.OnScrollListener listener)``` | Add a ```OnScrollListener``` to wrapped RecyclerView |
| ```removeOnScrollListener(RecyclerView.OnScrollListener listener)``` | Remove a ```OnScrollListener``` from wrapped RecyclerView |
| ```clearOnScrollListeners()``` | Remove all secondary ```OnScrollListener``` from wrapped RecyclerView |

<br />

## Changelog
See [changelog history].

#### Support ####
* * *
If you have any other questions regarding the use of this library, please contact us for support at info@cleveroad.com (email subject: "LoopBar. Support request.")
Also pull requests are welcome.

<br />
#### License ####
* * *
    The MIT License (MIT)
    
    Copyright (c) 2016 Cleveroad Inc.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
    
    
[ILoopBarPagerAdapter]: /LoopBar-widget/src/main/java/com/cleveroad/loopbar/adapter/ILoopBarPagerAdapter.java
[MenuLoopBarFragment]: /sample/src/main/java/com/cleveroad/sample/fragments/MenuLoopBarFragment.java
[ViewPagerLoopbarFragment]: /sample/src/main/java/com/cleveroad/sample/fragments/ViewPagerLoopBarFragment.java
[CategoriesAdapterLoopBarFragment]: /sample/src/main/java/com/cleveroad/sample/fragments/CategoriesAdapterLoopBarFragment.java
[ICategoryItem]: /LoopBar-widget/src/main/java/com/cleveroad/loopbar/adapter/ICategoryItem.java
[changelog history]: /CHANGELOG.md


