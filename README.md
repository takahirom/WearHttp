WearHttp
======
This library provides a means to access the content on the Web easily from AndroidWear.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-WearHttp-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/976)

## Example
### Code in AndroidWear app
```java
new WearGetText(MainActivity.this).get("http://example.com/text.txt", 
  new WearGetText.WearGetCallBack() {
    @Override
    public void onGet(String contents) {
        mTextView.setText(contents);
    }

    @Override
    public void onFail(final Exception e) {
        mTextView.setText(e.getMessage());
    }
});
new WearGetImage(MainActivity.this).get("https://example.com/image.png", 
  new WearGetImage.WearGetCallBack() {
    @Override
    public void onGet(Bitmap image) {
        mImageView.setImageBitmap(image);
    }

    @Override
    public void onFail(final Exception e) {
        mTextView.setText(e.getMessage());
    }
});
```
### AndroidWear screen shot  
![image](https://cloud.githubusercontent.com/assets/1386930/4348768/7b2bb5f0-419a-11e4-946b-1587e970b6e9.png)  

## Usage  
In Mobile and Wear module build.gradle:  
**You must be implemented in both the Mobile and Wear**
```groovy
dependencies {
    ...
    compile 'com.kogitune:wear-http:0.0.5'
}
```

In wear module.  
You implement it as in the [Example](#example)


### Example Project
[Example Project](https://github.com/takahirom/WearHttpSample)

## Suggestion

WearHttp can use in [WearSharedPreferences](https://github.com/takahirom/WearSharedPreferences) and simultaneous.



## License

This project is released under the Apache License, Version 2.0.

* [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
