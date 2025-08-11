// import 'package:agora_rtc_engine/agora_rtc_engine.dart';
// import 'package:get/get.dart';
// import 'package:logger/logger.dart';
// import 'package:permission_handler/permission_handler.dart';
//
// class ReceiverController  extends GetxController{
//
//
//   @override
//   void onReady() async {
//     await [Permission.microphone].request();
//
//     await _initializeAgoraVoiceSDK();
//     _setupEventHandlers();
//     await _joinChannel();
//   }
//
//   late RtcEngine _engine;
//
//   Future<void> _initializeAgoraVoiceSDK() async {
//     _engine = createAgoraRtcEngine();
//     await _engine.initialize(
//       const RtcEngineContext(
//         appId: "18881acb9f41436fa1758cf24024af1d",
//         channelProfile: ChannelProfileType.channelProfileCommunication,
//       ),
//     );
//   }
//
//   Future<void> _joinChannel() async {
//     await _engine.joinChannel(
//       token:
//       "007eJxTYJh2ZYXDS3OdE1HfXc2W5Nhxrcj57xLtYR8YyzfXJgx/++T4KDIYWFhaGiclJlmkmhibGZmmJhuamFslpRiYGRiaJaYYpEkJpGQ2BjAyr8wVYGRkgEMRnYyhOTEtMyWdgAABSdh6H",
//       channelId: "safado",
//       options: const ChannelMediaOptions(
//         autoSubscribeAudio: true,
//         // Automatically subscribe to all audio streams
//         publishMicrophoneTrack: true,
//         // Publish microphone-captured audio
//         clientRoleType: ClientRoleType.clientRoleAudience,
//       ),
//       uid: 0,
//     );
//   }
//
//   void _setupEventHandlers() {
//     _engine.registerEventHandler(
//       RtcEngineEventHandler(
//         onJoinChannelSuccess: (RtcConnection connection, int elapsed) {
//           Logger().e("Local user ${connection.localUid} joined");
//         },
//         onUserJoined: (RtcConnection connection, int remoteUid, int elapsed) {
//           Logger().e("Remote user $remoteUid joined");
//         },
//         onUserOffline: (
//             RtcConnection connection,
//             int remoteUid,
//             UserOfflineReasonType reason,
//             ) {
//           Logger().e("Remote user $remoteUid left");
//         },
//       ),
//     );
//   }
//
//   @override
//   void onClose()async {
//     await _engine.leaveChannel();
//     await _engine.release();
//     super.onClose();
//   }
// }