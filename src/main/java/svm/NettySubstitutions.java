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

import java.util.function.Predicate;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass( className = "io.netty.buffer.AbstractReferenceCountedByteBuf", onlyWith = PlatformHasClass.class )
final class TargetAbstractReferenceCountedByteBuf {

  @Alias
  @RecomputeFieldValue( kind = Kind.FieldOffset, //
                        declClassName = "io.netty.buffer.AbstractReferenceCountedByteBuf", //
                        name = "refCnt" ) //
  private static long REFCNT_FIELD_OFFSET;
}

@TargetClass( className = "io.netty.util.AbstractReferenceCounted", onlyWith = PlatformHasClass.class )
final class TargetAbstractReferenceCounted {

  @Alias
  @RecomputeFieldValue( kind = Kind.FieldOffset, //
                        declClassName = "io.netty.util.AbstractReferenceCounted", //
                        name = "refCnt" ) //
  private static long REFCNT_FIELD_OFFSET;
}

/**
 * A predicate to tell whether this platform includes the argument class.
 */
final class PlatformHasClass implements Predicate<String> {

  @Override
  public boolean test( String className ) {
    try {
      @SuppressWarnings( { "unused" } ) final Class<?> classForName = Class.forName( className );
      return true;
    } catch ( ClassNotFoundException cnfe ) {
      return false;
    }
  }
}

