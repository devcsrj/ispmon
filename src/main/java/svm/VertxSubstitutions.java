package svm;

import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.impl.resolver.DefaultResolverProvider;
import io.vertx.core.net.impl.transport.Transport;
import io.vertx.core.spi.resolver.ResolverProvider;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * This substitution avoid having jcraft zlib added to the build
 */
@TargetClass( className = "io.netty.handler.codec.compression.ZlibCodecFactory" )
final class TargetZlibCodecFactory {

  @Substitute
  public static ZlibEncoder newZlibEncoder( int compressionLevel ) {
    return new JdkZlibEncoder( compressionLevel );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( ZlibWrapper wrapper ) {
    return new JdkZlibEncoder( wrapper );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( ZlibWrapper wrapper, int compressionLevel ) {
    return new JdkZlibEncoder( wrapper, compressionLevel );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel ) {
    return new JdkZlibEncoder( wrapper, compressionLevel );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( byte[] dictionary ) {
    return new JdkZlibEncoder( dictionary );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( int compressionLevel, byte[] dictionary ) {
    return new JdkZlibEncoder( compressionLevel, dictionary );
  }

  @Substitute
  public static ZlibEncoder newZlibEncoder( int compressionLevel, int windowBits, int memLevel, byte[] dictionary ) {
    return new JdkZlibEncoder( compressionLevel, dictionary );
  }

  @Substitute
  public static ZlibDecoder newZlibDecoder() {
    return new JdkZlibDecoder( true );
  }

  @Substitute
  public static ZlibDecoder newZlibDecoder( ZlibWrapper wrapper ) {
    return new JdkZlibDecoder( wrapper, true );
  }

  @Substitute
  public static ZlibDecoder newZlibDecoder( byte[] dictionary ) {
    return new JdkZlibDecoder( dictionary );
  }
}

/**
 * This substitution forces the usage of the blocking DNS resolver
 */
@TargetClass( className = "io.vertx.core.spi.resolver.ResolverProvider" )
final class TargetResolverProvider {

  @Substitute
  public static ResolverProvider factory( Vertx vertx, AddressResolverOptions options ) {
    return new DefaultResolverProvider();
  }
}

/**
 * This prevents substrate from using a native OS transport
 */
@TargetClass( className = "io.vertx.core.net.impl.transport.Transport" )
final class TargetTransport {

  @Substitute
  public static Transport transport( boolean preferNative ) {
    return Transport.JDK;
  }
}
