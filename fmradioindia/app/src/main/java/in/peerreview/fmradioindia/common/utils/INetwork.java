package in.peerreview.fmradioindia.common.utils;

public interface INetwork {
  void retrive(
      final String url,
      Network.CacheControl cacheControl,
      final Network.INetworkCallback networkCallback);

  void send(final String url, final Network.INetworkCallback networkCallback);
}
