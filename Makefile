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
	@echo "build server"
	@javac ${ServerSrc} -d $(TargetPath)
	@jar cvfm $@ $(ServerManiFest) -C ${TargetPath} Server
	@echo "build server done"

$(ClientJar): $(ClientSrc)
	@echo "build client"
	@javac $^ -d $(TargetPath)
	@jar cvfm $@ $(ClientManiFest) -C ${TargetPath} Client
	@echo "build client done"

phony += run
run: run_server run_client

phony += server
run_server: $(ServerJar)
	java -jar $^

phony += client
run_client: $(ClientJar)
	java -jar $^

phony += pre
pre:
	@mkdir -p ${TargetPath}

phony += clean
clean:
	@rm -rf ${TargetPath}
	@echo "clean done"

.PHONY: $(phony)
