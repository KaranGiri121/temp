import 'package:get/get.dart';
import 'package:logger/logger.dart';

class BuildController extends GetxController{


  @override
  void onClose() {
    Logger().e("OnClose");
    super.onClose();
  }
}