# Makefile
#
#
SrcPath := src
TargetPath := target
ServerTarget := ${TargetPath}/Server/
ServerTarget := ${TargetPath}/Server/
ServerJar := ${TargetPath}/Server.jar
ClientJar := ${TargetPath}/client.jar

ServerSrc := $(wildcard ${SrcPath}/Server/*.java)
ClientSrc := $(wildcard ${SrcPath}/Client/*.java)

ServerManiFest := src/Server/MANIFEST.MF
ClientManiFest := src/Client/MANIFEST.MF


default: all

phony := all
all: pre $(ServerJar) $(ClientJar)
	@echo "all done"

$(ServerJar): $(ServerSrc)
	@javac ${ServerSrc} -d $(TargetPath)
	@jar cvfm $@ $(ServerManiFest) -C ${TargetPath} Server

$(ClientJar): $(ClientSrc)
	@javac $^ -d $(TargetPath)
	@jar cvfm $@ $(ClientManiFest) -C ${TargetPath} Client

phony += run
run: server client

phony += server
server: $(ServerJar)
	java -jar $^

phony += client
client: $(ClientJar)
	java -jar $^

phony += pre
pre:
	mkdir -p ${TargetPath}

phony += clean
clean:
	@rm -rf ${TargetPath}

.PHONY: $(phony)