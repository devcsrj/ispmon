/**
 * MIT License
 *
 * Copyright (c) 2019 Reijhanniel Jearl Campos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
