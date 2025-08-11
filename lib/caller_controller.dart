//
// import 'package:get/get.dart';
// import 'package:logger/logger.dart';
// import 'package:permission_handler/permission_handler.dart';
//
// class CallerController extends GetxController {
//   @override
//   void onReady() async {
//     await [Permission.microphone].request();
//     await _initializeAgoraVoiceSDK();
//     _setupEventHandlers();
//     await _joinChannel();
//     super.onReady();
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
//       token: "007eJxTYJh2ZYXDS3OdE1HfXc2W5Nhxrj57xLtYR8YyzfXJgx/++T4KDIYWFhaGiclJlmkmhibGZmmJhuamFslpRiYGRiaJaYYpEkJpGQ2BjAyr8wVYGRkgEMRnYyhOTEtMyWdgAABSdh6H",
//       channelId: "safado",
//       options: const ChannelMediaOptions(
//         autoSubscribeAudio: true,
//         publishMicrophoneTrack: true,
//         clientRoleType: ClientRoleType.clientRoleBroadcaster,
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
//
//           // setState(() {
//           //   _remoteUid = remoteUid; // Store remote user ID
//           // });
//         },
//         onUserOffline: (
//           RtcConnection connection,
//           int remoteUid,
//           UserOfflineReasonType reason,
//         ) {
//           Logger().e("Remote user $remoteUid left");
//           // setState(() {
//           //   _remoteUid = null; // Remove remote user ID
//           // });
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
