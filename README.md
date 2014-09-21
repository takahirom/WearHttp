WearHttp
======
This library provides an http contents getter.

## Example
### code
```java
new WearGetTextContents(context).getContents("http://headers.jsontest.com/", new WearGetTextContents.WearGetContentsCallBack() {
    @Override
    public void onGetContents(String contents) {
        mTextView.setText(contents);
    }

    @Override
    public void onFail(final Exception e) {
        mTextView.setText(e.getMessage());
    }
}, 10);
new WearGetImageContents(context).getContents("https://cloud.githubusercontent.com/assets/1386930/4347967/65f420c4-4176-11e4-8cb6-d70f1867f8cb.png",
    new WearGetImageContents.WearGetContentsCallBack() {
        @Override
        public void onGetContents(Bitmap image) {
            mImageView.setImageBitmap(image);
        }

        @Override
        public void onFail(final Exception e) {
            mTextView.setText(e.getMessage());
        }
    }, 10);
```
### AndroidWear Screen  
![image](https://cloud.githubusercontent.com/assets/1386930/4348768/7b2bb5f0-419a-11e4-946b-1587e970b6e9.png)  



## License

This project is released under the Apache License, Version 2.0.

* [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
