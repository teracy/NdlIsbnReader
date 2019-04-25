# このプロジェクトについて

教育用に[Firebase ML Kit](https://firebase.google.com/docs/ml-kit/)を利用するアプリのサンプルとして作成したもの。

ML Kitのバーコードスキャン機能を利用して書籍のISBNを認識し、更に[国会図書館サーチAPI](https://iss.ndl.go.jp/information/api/riyou/)を利用して書籍の情報を表示する。

Firebaseを利用しているため、別途Firebaseのコンソールからgoogle-services.jsonを取得してappモジュール直下に導入する必要がある。（[参考](https://dev.classmethod.jp/machine-learning/mlkit_detection_demo/)）

# 参考にしたもの

## ML Kit

* [Scan Barcodes with ML Kit on Android  |  Firebase](https://firebase.google.com/docs/ml-kit/android/read-barcodes)
* [firebase/quickstart-android: Firebase Quickstart Samples for Android](https://github.com/firebase/quickstart-android)
* [googlesamples/android-Camera2Basic](https://github.com/googlesamples/android-Camera2Basic)
* [Detect Barcodes in an Image using Firebase MLKit](https://codelabs.developers.google.com/codelabs/mlkit-barcode-android/index.html#0)
* [ML Kit For Firebaseを使ってスマホで色々検出してみた ｜ DevelopersIO](https://dev.classmethod.jp/machine-learning/mlkit_detection_demo/)
* [Android, Kotlinでカメラ(camera2)を表示するための最小実装 - Qiita](https://qiita.com/k-boy/items/3b64c4e9921e29cc4471)
    * [kboy-silvergym/MLKitSample: Firebase MLKit Sample for Swift (iOS) & Android (Kotlin)](https://github.com/kboy-silvergym/MLKitSample)
* [Camera2 APIを使いこなす(Part 1 : プレビューの表示) – Tsukamoto Takeshi – Medium](https://medium.com/@itometeam/camera2-api%E3%82%92%E4%BD%BF%E3%81%84%E3%81%93%E3%81%AA%E3%81%99-part-1-%E3%83%97%E3%83%AC%E3%83%93%E3%83%A5%E3%83%BC%E3%81%AE%E8%A1%A8%E7%A4%BA-e5e799a7b4dd)

## 国会図書館サーチAPI

* [国立国会図書館サーチ 外部提供インタフェース仕様書](https://iss.ndl.go.jp/information/wp-content/uploads/2018/09/ndlsearch_api_20180925_jp.pdf)
* [SimpleXmlConverterFactory deprecated without replacement for Android · Issue #2733 · square/retrofit](https://github.com/square/retrofit/issues/2733)
* [Tickaroo/tikxml: Modern XML Parser for Android](https://github.com/Tickaroo/tikxml)

## その他

* [AndroidのKotlinでViewのレイアウト完了時処理を簡単に書く方法 - Qiita](https://qiita.com/titoi2/items/7bf271cd17beae74620b)
* [permissions-dispatcher/PermissionsDispatcher: Simple annotation-based API to handle runtime permissions.](https://github.com/permissions-dispatcher/PermissionsDispatcher)

# ライセンス

このプロジェクト自体は下記の通りだが、国会図書館サーチAPIの利用に関しては[利用規約](https://iss.ndl.go.jp/information/api/)に従うこと。

```
Copyright (C) 2019 teracy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
