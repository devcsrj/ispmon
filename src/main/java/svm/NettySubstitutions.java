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

