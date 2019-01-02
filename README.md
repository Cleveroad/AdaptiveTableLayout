# AdaptiveTableLayout [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="19"></a>
![Header image](/images/header.png)

## Welcome the new CSV Library AdaptiveTableLayout for Android by Cleveroad

Pay your attention to our new library that makes it possible to read, edit and write CSV files. If you use Android-based device, you can easily use our library for implementation of all aforementioned actions. In addition, you will be able to change rows and columns, display the picture via link, and align the required data. It will surely help you cope with your tasks faster and make your output higher. AdaptiveTableLayout library is at your disposal.

![Demo image](/images/demo.gif)

#### Take a look at the animation of <strong><a target="_blank" href="https://www.youtube.com/watch?v=YTwpEPIlhuE">AdaptiveTableLayout for Android on YouTube</a></strong> in HD quality. For using this library in a valuable way, you can find our CSV Editor app on the <a target="_blank"  href="https://play.google.com/store/apps/details?id=com.cleveroad.tablelayout">Google Play Store</a> or on <a target="_blank"  href="https://appetize.io/app/wgacjavwr57fec241bq802gzcg?device=nexus5&scale=75&orientation=portrait&osVersion=7.0">Appetize</a>.
[![Awesome](/images/youtube.png)](https://www.youtube.com/watch?v=YTwpEPIlhuE)[![Awesome](/images/google-play.png)](https://play.google.com/store/apps/details?id=com.cleveroad.tablelayout)[![Awesome](/images/appertize.png)](https://appetize.io/app/wgacjavwr57fec241bq802gzcg?device=nexus5&scale=75&orientation=portrait&osVersion=7.0)

The main goal of the library is to apply all its functions in the process of working with CSV files. Moreover, it will give you a competitive edge over others. 

[![Awesome](/images/logo-footer.png)](https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts)
<br/>
## Setup and usage
### Installation
by gradle : 
```groovy
dependencies {
    implementation "com.cleveroad:adaptivetablelayout:1.2.1"
}
```
### Features ###
Library consists of three parts:
- AdaptiveTableLayout (View)
- LinkedAdaptiveTableAdapter (Adapter)
- ViewHolderImpl (ViewHolder)

### Usage ###
#### AdaptiveTableLayout ####
```XML
  <com.cleveroad.adaptivetablelayout.AdaptiveTableLayout
        android:id="@+id/tableLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"      
        app:cellMargin="1dp"
        app:fixedHeaders="true"
        app:solidRowHeaders="true"
        app:dragAndDropEnabled="true"/>
```
|  attribute name | description |
|---|---|
| cellMargin  | margin between cards |
| fixedHeaders  | fixed headers mode. If enable, headers always will be displayed in the corners. |
| solidRowHeaders  | solid row headers mode. If enable, row header will change its position with dragging row. |
| dragAndDropEnabled | drag and drop mode. If enable, column or row will change its position with dragging after long press on row or column header. |

```groovy
// Return fixed headers mode
boolean isHeaderFixed(); 

// Return solid row headers mode
boolean isSolidRowHeader()

// Return drag and drop mode
boolean isDragAndDropEnabled()

// Return true if layout direction is RightToLeft
boolean isRTL()

// Set fixed headers mode
void setHeaderFixed(boolean headerFixed)

// Set solid row headers mode
void setSolidRowHeader(boolean solidRowHeader)
1.2.0
// Set drag and drop mode
void setDragAndDrow(boolean enabled)

/**
 * Set adapter with IMMUTABLE data.
 * Create wrapper with links between layout rows, columns and data rows, columns.
 * On drag and drop event just change links but not change data in adapter.
 */
void setAdapter(@Nullable AdaptiveTableAdapter adapter)

/**
 * Set adapter with MUTABLE data.
 * You need to implement switch rows and columns methods.    
 * DO NOT USE WITH BIG DATA!!
 */
void setAdapter(@Nullable DataAdaptiveTableLayoutAdapter adapter)

// Notify any registered observers that the data set has changed.
void notifyDataSetChanged()

// Notify any registered observers that the item has changed.
void notifyItemChanged(int rowIndex, int columnIndex)

// Notify any registered observers that the row with rowIndex has changed.
void notifyRowChanged(int rowIndex)

// Notify any registered observers that the column with columnIndex has changed.
void notifyColumnChanged(int columnIndex)
```
#### Adapter ####
You could use adapter interfaces: AdaptiveTableAdapter and DataAdaptiveTableLayoutAdapter. But to simplify the usage, library contains base adapters: <b>BaseDataAdaptiveTableLayoutAdapter</b> and <b>LinkedAdaptiveTableAdapter</b>.

<b>BaseDataAdaptiveTableLayoutAdapter</b> - simple adapter which works with light data. WARNING! on each row/column switch, original data will be changed. 

<b>LinkedAdaptiveTableAdapter</b> - adapter which works with heavy data. WARNING! This type of adapter doesn't change original data. It contains matrix with changed items with links on it. To get changed data you need use AdaptiveTableLayout.getLinkedAdapterRowsModifications() and AdaptiveTableLayout.getLinkedAdapterColumnsModifications().
Don't forget to check AdaptiveTableLayout.isSolidRowHeader() flag. If it's false, you need to ignore switching first elemet in each row.

<b>For both adapters you need to know all rows/columns widths, heights and rows/columns count before set adapter to AdaptiveTableLayout.</b>
#### Fragment/Activity usage ####
```groovy
mTableLayout = (AdaptiveTableLayout) view.findViewById(R.id.tableLayout);
...
mTableAdapter = new SampleLinkedTableAdapter(getContext(), mCsvFileDataSource);
mTableAdapter.setOnItemClickListener(...);
mTableAdapter.setOnItemLongClickListener(...);
mTableLayout.setAdapter(mTableAdapter);
...
mTableLayout.setHeaderFixed(true);
mTableLayout.setSolidRowHeader(true);
mTableAdapter.notifyDataSetChanged();
```
#### XML usage ####
```groovy
 <com.cleveroad.adaptivetablelayout.AdaptiveTableLayout
        android:id="@+id/tableLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/toolbar"
        app:cellMargin="1dp"
        app:fixedHeaders="true"
        app:solidRowHeaders="true"
        app:dragAndDropEnabled="true"/>
```
#### Adapter usage ####
<a href="sample/src/main/java/com/cleveroad/sample/adapter/SampleLinkedTableAdapter.java"> Adapter sample </a>

## Changelog
See [changelog history].

### Support ###
If you have any questions, issues or propositions, please create a <a href="../../issues/new">new issue</a> in this repository.

If you want to hire us, send an email to sales@cleveroad.com or fill the form on <a href="https://www.cleveroad.com/contact">contact page</a>

Follow us:

[![Awesome](/images/social/facebook.png)](https://www.facebook.com/cleveroadinc/)   [![Awesome](/images/social/twitter.png)](https://twitter.com/cleveroadinc)   [![Awesome](/images/social/google.png)](https://plus.google.com/+CleveroadInc)   [![Awesome](/images/social/linkedin.png)](https://www.linkedin.com/company/cleveroad-inc-)   [![Awesome](/images/social/youtube.png)](https://www.youtube.com/channel/UCFNHnq1sEtLiy0YCRHG2Vaw)
<br/>
### License ###
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

[changelog history]: /CHANGELOG.md
