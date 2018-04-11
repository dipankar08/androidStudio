package in.co.dipankar.ping.common.webrtc;

/** Created by dip on 3/25/18. */
public class Constant {
  // Tracks
  public static final String VIDEO_TRACK_ID = "ARDAMSv0";
  public static final String AUDIO_TRACK_ID = "ARDAMSa0";
  public static final String VIDEO_TRACK_TYPE = "video";

  // Codes
  public static final String VIDEO_CODEC_VP8 = "VP8";
  public static final String VIDEO_CODEC_VP9 = "VP9";
  public static final String VIDEO_CODEC_H264 = "H264";
  public static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
  public static final String VIDEO_CODEC_H264_HIGH = "H264 High";
  public static final String AUDIO_CODEC_OPUS = "opus";
  public static final String AUDIO_CODEC_ISAC = "ISAC";

  // Bit rates
  public static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
  public static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";

  // Video Extra Info
  public static final String VIDEO_FLEXFEC_FIELDTRIAL =
      "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
  public static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
  public static final String VIDEO_H264_HIGH_PROFILE_FIELDTRIAL = "WebRTC-H264HighProfile/Enabled/";
  public static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
      "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";

  // Addio and Video Advance Control
  public static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
  public static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
  public static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
  public static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
  public static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
  public static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
}
