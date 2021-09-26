@echo off
protoc -I=src\main\java\optic_fusion1\server --java_out=src\main\java src\main\java\optic_fusion1\server\ChatRoomProtocol.proto