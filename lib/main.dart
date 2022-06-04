import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('my_java_linker');

  String result = 'no result';
  Uint8List? bytes;

  Future<void> excuteJavaCode() async {
    final ByteData imageData = await NetworkAssetBundle(Uri.parse(
            "https://cdn.dribbble.com/users/7338576/screenshots/15684153/media/a52985d53636f23a01bed622925099c3.jpg?compress=1&resize=400x300"))
        .load("");
    bytes = imageData.buffer.asUint8List();
    setState(() {});
    try {
      String backVal =
          await platform.invokeMethod('myJavaFunc', {'data': bytes});
      result = 'the result back :$backVal';
    } on PlatformException catch (e) {
      result = 'something wrong: $e';
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
          child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          bytes != null ? Image.memory(bytes!) : Container(),
          Text(result),
          TextButton(
            onPressed: excuteJavaCode,
            child: const Text('run java code'),
          )
        ],
      )),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
