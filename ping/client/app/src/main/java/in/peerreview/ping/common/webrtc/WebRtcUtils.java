package in.peerreview.ping.common.webrtc;

import static android.content.ContentValues.TAG;
import static in.peerreview.ping.common.webrtc.Constant.AUDIO_CODEC_PARAM_BITRATE;
import static in.peerreview.ping.common.webrtc.Constant.VIDEO_CODEC_PARAM_START_BITRATE;

import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.webrtc.StatsReport;

/** Created by dip on 3/10/18. */
public final class WebRtcUtils {

  public static Map<String, String> parseStatistics(final StatsReport[] reports) {

    boolean videoCallEnabled = true;
    Map<String, String> result = new HashMap<>();

    StringBuilder encoderStat = new StringBuilder(128);
    StringBuilder bweStat = new StringBuilder();
    StringBuilder connectionStat = new StringBuilder();
    StringBuilder videoSendStat = new StringBuilder();
    StringBuilder videoRecvStat = new StringBuilder();
    String fps = null;
    String targetBitrate = null;
    String actualBitrate = null;

    for (StatsReport report : reports) {
      if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("send")) {
        // Send video statistics.
        Map<String, String> reportMap = getReportMap(report);
        String trackId = reportMap.get("googTrackId");
        if (trackId != null && trackId.contains(Constant.VIDEO_TRACK_ID)) {
          fps = reportMap.get("googFrameRateSent");
          videoSendStat.append(report.id).append("\n");
          for (StatsReport.Value value : report.values) {
            String name = value.name.replace("goog", "");
            videoSendStat.append(name).append("=").append(value.value).append("\n");
          }
        }
      } else if (report.type.equals("ssrc")
          && report.id.contains("ssrc")
          && report.id.contains("recv")) {
        // Receive video statistics.
        Map<String, String> reportMap = getReportMap(report);
        // Check if this stat is for video track.
        String frameWidth = reportMap.get("googFrameWidthReceived");
        if (frameWidth != null) {
          videoRecvStat.append(report.id).append("\n");
          for (StatsReport.Value value : report.values) {
            String name = value.name.replace("goog", "");
            videoRecvStat.append(name).append("=").append(value.value).append("\n");
          }
        }
      } else if (report.id.equals("bweforvideo")) {
        // BWE statistics.
        Map<String, String> reportMap = getReportMap(report);
        targetBitrate = reportMap.get("googTargetEncBitrate");
        actualBitrate = reportMap.get("googActualEncBitrate");

        bweStat.append(report.id).append("\n");
        for (StatsReport.Value value : report.values) {
          String name = value.name.replace("goog", "").replace("Available", "");
          bweStat.append(name).append("=").append(value.value).append("\n");
        }
      } else if (report.type.equals("googCandidatePair")) {
        // Connection statistics.
        Map<String, String> reportMap = getReportMap(report);
        String activeConnection = reportMap.get("googActiveConnection");
        if (activeConnection != null && activeConnection.equals("true")) {
          connectionStat.append(report.id).append("\n");
          for (StatsReport.Value value : report.values) {
            String name = value.name.replace("goog", "");
            connectionStat.append(name).append("=").append(value.value).append("\n");
          }
        }
      }
    }
    result.put("bweStat", bweStat.toString());
    result.put("connectionStat", connectionStat.toString());
    result.put("videoSendStat", videoSendStat.toString());
    result.put("videoRecvStat", videoRecvStat.toString());

    if (videoCallEnabled) {
      if (fps != null) {
        encoderStat.append("Fps:  ").append(fps).append("\n");
      }
      if (targetBitrate != null) {
        encoderStat.append("Target BR: ").append(targetBitrate).append("\n");
      }
      if (actualBitrate != null) {
        encoderStat.append("Actual BR: ").append(actualBitrate).append("\n");
      }
    }
    /*
    if (cpuMonitor != null) {
        encoderStat.append("CPU%: ")
                .append(cpuMonitor.getCpuUsageCurrent())
                .append("/")
                .append(cpuMonitor.getCpuUsageAverage())
                .append(". Freq: ")
                .append(cpuMonitor.getFrequencyScaleAverage());
    }
    */
    result.put("encoderStat", encoderStat.toString());
    return result;
  }

  private static Map<String, String> getReportMap(StatsReport report) {
    Map<String, String> reportMap = new HashMap<String, String>();
    for (StatsReport.Value value : report.values) {
      reportMap.put(value.name, value.value);
    }
    return reportMap;
  }

  public static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
    final String[] lines = sdpDescription.split("\r\n");
    final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
    if (mLineIndex == -1) {
      Log.w(TAG, "No mediaDescription line, so can't prefer " + codec);
      return sdpDescription;
    }
    // A list with all the payload types with name |codec|. The payload types are integers in the
    // range 96-127, but they are stored as strings here.
    final List<String> codecPayloadTypes = new ArrayList<String>();
    // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
    final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
    for (int i = 0; i < lines.length; ++i) {
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        codecPayloadTypes.add(codecMatcher.group(1));
      }
    }
    if (codecPayloadTypes.isEmpty()) {
      Log.w(TAG, "No payload types with name " + codec);
      return sdpDescription;
    }

    final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
    if (newMLine == null) {
      return sdpDescription;
    }
    Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
    lines[mLineIndex] = newMLine;
    return _joinString(Arrays.asList(lines), "\r\n", true /* delimiterAtEnd */);
  }

  public static String setStartBitrate(
      String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
    String[] lines = sdpDescription.split("\r\n");
    int rtpmapLineIndex = -1;
    boolean sdpFormatUpdated = false;
    String codecRtpMap = null;
    // Search for codec rtpmap in format
    // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
    String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
    Pattern codecPattern = Pattern.compile(regex);
    for (int i = 0; i < lines.length; i++) {
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        codecRtpMap = codecMatcher.group(1);
        rtpmapLineIndex = i;
        break;
      }
    }
    if (codecRtpMap == null) {
      Log.w(TAG, "No rtpmap for " + codec + " codec");
      return sdpDescription;
    }
    Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

    // Check if a=fmtp string already exist in remote SDP for this codec and
    // update it with new bitrate parameter.
    regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
    codecPattern = Pattern.compile(regex);
    for (int i = 0; i < lines.length; i++) {
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        Log.d(TAG, "Found " + codec + " " + lines[i]);
        if (isVideoCodec) {
          lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
        } else {
          lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
        }
        Log.d(TAG, "Update remote SDP line: " + lines[i]);
        sdpFormatUpdated = true;
        break;
      }
    }

    StringBuilder newSdpDescription = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      newSdpDescription.append(lines[i]).append("\r\n");
      // Append new a=fmtp line if no such line exist for a codec.
      if (!sdpFormatUpdated && i == rtpmapLineIndex) {
        String bitrateSet;
        if (isVideoCodec) {
          bitrateSet =
              "a=fmtp:" + codecRtpMap + " " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
        } else {
          bitrateSet =
              "a=fmtp:"
                  + codecRtpMap
                  + " "
                  + AUDIO_CODEC_PARAM_BITRATE
                  + "="
                  + (bitrateKbps * 1000);
        }
        Log.d(TAG, "Add remote SDP line: " + bitrateSet);
        newSdpDescription.append(bitrateSet).append("\r\n");
      }
    }
    return newSdpDescription.toString();
  }
  // Helper
  private static String _joinString(
      Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
    Iterator<? extends CharSequence> iter = s.iterator();
    if (!iter.hasNext()) {
      return "";
    }
    StringBuilder buffer = new StringBuilder(iter.next());
    while (iter.hasNext()) {
      buffer.append(delimiter).append(iter.next());
    }
    if (delimiterAtEnd) {
      buffer.append(delimiter);
    }
    return buffer.toString();
  }

  private static String movePayloadTypesToFront(List<String> preferredPayloadTypes, String mLine) {
    // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
    final List<String> origLineParts = Arrays.asList(mLine.split(" "));
    if (origLineParts.size() <= 3) {
      Log.e(TAG, "Wrong SDP media description format: " + mLine);
      return null;
    }
    final List<String> header = origLineParts.subList(0, 3);
    final List<String> unpreferredPayloadTypes =
        new ArrayList<String>(origLineParts.subList(3, origLineParts.size()));
    unpreferredPayloadTypes.removeAll(preferredPayloadTypes);
    // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
    // types.
    final List<String> newLineParts = new ArrayList<String>();
    newLineParts.addAll(header);
    newLineParts.addAll(preferredPayloadTypes);
    newLineParts.addAll(unpreferredPayloadTypes);
    return _joinString(newLineParts, " ", false /* delimiterAtEnd */);
  }
  /** Returns the line number containing "m=audio|video", or -1 if no such line exists. */
  private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
    final String mediaDescription = isAudio ? "m=audio " : "m=video ";
    for (int i = 0; i < sdpLines.length; ++i) {
      if (sdpLines[i].startsWith(mediaDescription)) {
        return i;
      }
    }
    return -1;
  }
}
