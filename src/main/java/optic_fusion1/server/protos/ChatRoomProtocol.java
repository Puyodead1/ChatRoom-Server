// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ChatRoomProtocol.proto

package optic_fusion1.server.protos;

public final class ChatRoomProtocol {
  private ChatRoomProtocol() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Packet_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Packet_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026ChatRoomProtocol.proto\"\231\001\n\006Packet\022\'\n\013p" +
      "acket_type\030\001 \002(\0162\022.Packet.PacketType\022*\n\020" +
      "protocol_version\030\002 \002(\0162\020.ProtocolVersion" +
      "\022\035\n\016use_encryption\030\003 \001(\010:\005false\"\033\n\nPacke" +
      "tType\022\r\n\tHANDSHAKE\020\000*\"\n\017ProtocolVersion\022" +
      "\017\n\013VERSION_000\020\000B1\n\033optic_fusion1.server" +
      ".protosB\020ChatRoomProtocolP\001"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Packet_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Packet_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Packet_descriptor,
        new java.lang.String[] { "PacketType", "ProtocolVersion", "UseEncryption", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
