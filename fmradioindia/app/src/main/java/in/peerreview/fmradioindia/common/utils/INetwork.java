package in.peerreview.fmradioindia.common.utils;

import java.util.Map;

public interface INetwork {
  void retrive(
      final String url,
      Network.CacheControl cacheControl,
      final Network.INetworkCallback networkCallback);

  void send(
      final String url,
      final Map<String, String> data,
      final Network.INetworkCallback networkCallback);
}
