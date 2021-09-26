// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ChatRoomProtocol.proto

package optic_fusion1.server.protos;

/**
 * Protobuf enum {@code ProtocolVersion}
 */
public enum ProtocolVersion
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>VERSION_000 = 0;</code>
   */
  VERSION_000(0),
  ;

  /**
   * <code>VERSION_000 = 0;</code>
   */
  public static final int VERSION_000_VALUE = 0;


  public final int getNumber() {
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static ProtocolVersion valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static ProtocolVersion forNumber(int value) {
    switch (value) {
      case 0: return VERSION_000;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<ProtocolVersion>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      ProtocolVersion> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<ProtocolVersion>() {
          public ProtocolVersion findValueByNumber(int number) {
            return ProtocolVersion.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return optic_fusion1.server.protos.ChatRoomProtocol.getDescriptor().getEnumTypes().get(0);
  }

  private static final ProtocolVersion[] VALUES = values();

  public static ProtocolVersion valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private ProtocolVersion(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:ProtocolVersion)
}

