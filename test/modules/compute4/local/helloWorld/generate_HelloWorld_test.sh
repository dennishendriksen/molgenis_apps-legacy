workdir=$( cd -P "$( dirname "$0" )" && pwd )
. $workdir/initialize.sh

CP="$workdir/../../../../../.."

java -cp $CP/molgenis_apps/build/classes:$CP/molgenis/bin:\
$CP/molgenis/lib/ant-1.8.1.jar:\
$CP/molgenis/lib/ant-apache-log4j.jar:\
$CP/molgenis/lib/aopalliance-1.0.jar:\
$CP/molgenis/lib/arq.jar:\
$CP/molgenis/lib/asm-3.3.jar:\
$CP/molgenis/lib/axiom-api-1.2.7.jar:\
$CP/molgenis/lib/axiom-impl-1.2.7.jar:\
$CP/molgenis/lib/bcprov-jdk15-1.43.jar:\
$CP/molgenis/lib/commons-codec-1.3.jar:\
$CP/molgenis/lib/commons-collections-3.2.1.jar:\
$CP/molgenis/lib/commons-dbcp-1.2.1.jar:\
$CP/molgenis/lib/commons-email-1.2.jar:\
$CP/molgenis/lib/commons-fileupload-1.1.jar:\
$CP/molgenis/lib/commons-io-2.4.jar:\
$CP/molgenis/lib/commons-lang-2.5.jar:\
$CP/molgenis/lib/commons-lang3-3.1.jar:\
$CP/molgenis/lib/commons-logging-1.1.1.jar:\
$CP/molgenis/lib/commons-pool-1.5.2.jar:\
$CP/molgenis/lib/concurrent.jar:\
$CP/molgenis/lib/cxf-bundle-minimal-2.5.2.jar:\
$CP/molgenis/lib/d2rq-0.7.jar:\
$CP/molgenis/lib/d2r-server-0.7.jar:\
$CP/molgenis/lib/dom4j-1.6.1.jar:\
$CP/molgenis/lib/FastInfoset-1.2.7.jar:\
$CP/molgenis/lib/freemarker.jar:\
$CP/molgenis/lib/ganymed-ssh2-build250.jar:\
$CP/molgenis/lib/gson-2.2.1.jar:\
$CP/molgenis/lib/hsqldb.jar:\
$CP/molgenis/lib/icu4j_3_4.jar:\
$CP/molgenis/lib/iri.jar:\
$CP/molgenis/lib/jakarta-oro-2.0.8.jar:\
$CP/molgenis/lib/javassist-3.12.0.GA.jar:\
$CP/molgenis/lib/javax.mail.jar:\
$CP/molgenis/lib/javax.servlet.jar:\
$CP/molgenis/lib/jaxb-impl-2.2.1.1.jar:\
$CP/molgenis/lib/jaxb-xjc-2.2.1.1.jar:\
$CP/molgenis/lib/jaxen-1.1.jar:\
$CP/molgenis/lib/jdom-1.0.jar:\
$CP/molgenis/lib/jena.jar:\
$CP/molgenis/lib/jersey-json-1.1.5.jar:\
$CP/molgenis/lib/jettison-1.2.jar:\
$CP/molgenis/lib/jetty-6.1.21.jar:\
$CP/molgenis/lib/jetty-html-6.1.21.jar:\
$CP/molgenis/lib/jetty-plus-6.1.21.jar:\
$CP/molgenis/lib/jetty-util-6.1.21.jar:\
$CP/molgenis/lib/jopenid-1.07.jar:\
$CP/molgenis/lib/joseki.jar:\
$CP/molgenis/lib/jra-1.0-alpha-4.jar:\
$CP/molgenis/lib/js-1.7R1.jar:\
$CP/molgenis/lib/json.jar:\
$CP/molgenis/lib/jsr311-api-1.1.1.jar:\
$CP/molgenis/lib/jta-1.1.jar:\
$CP/molgenis/lib/junit-4.8.2.jar:\
$CP/molgenis/lib/jxl.jar:\
$CP/molgenis/lib/keggapi.jar:\
$CP/molgenis/lib/log4j-1.2.15.jar:\
$CP/molgenis/lib/lucene-core-3.0.2.jar:\
$CP/molgenis/lib/mail.jar:\
$CP/molgenis/lib/mindterm.jar:\
$CP/molgenis/lib/mockito-all-1.9.5.jar:\
$CP/molgenis/lib/mysql-connector-java-5.1.2-beta-bin.jar:\
$CP/molgenis/lib/neethi-2.0.4.jar:\
$CP/molgenis/lib/ontocat-0.9.9.1.jar:\
$CP/molgenis/lib/org.semanticweb.HermiT.jar:\
$CP/molgenis/lib/oro-2.0.8.jar:\
$CP/molgenis/lib/postgresql-8.3-603.jdbc4.jar:\
$CP/molgenis/lib/quartz-1.6.0.jar:\
$CP/molgenis/lib/selenium-server-standalone-2.25.0.jar:\
$CP/molgenis/lib/serializer-2.7.1.jar:\
$CP/molgenis/lib/simplecaptcha-1.2.1.jar:\
$CP/molgenis/lib/slf4j-api-1.6.1.jar:\
$CP/molgenis/lib/slf4j-log4j12-1.6.1.jar:\
$CP/molgenis/lib/smtp.jar:\
$CP/molgenis/lib/spring-core-3.1.2.RELEASE.jar:\
$CP/molgenis/lib/spring-test-3.1.2.RELEASE.jar:\
$CP/molgenis/lib/stax-utils-20060502.jar:\
$CP/molgenis/lib/tar.jar:\
$CP/molgenis/lib/testng-5.14.10.jar:\
$CP/molgenis/lib/tjws-1.99.jar:\
$CP/molgenis/lib/validation-api-1.0.0.GA.jar:\
$CP/molgenis/lib/velocity-1.6.4.jar:\
$CP/molgenis/lib/wsdl4j-1.6.2.jar:\
$CP/molgenis/lib/wss4j-1.5.8.jar:\
$CP/molgenis/lib/wstx-asl-3.2.8.jar:\
$CP/molgenis/lib/xalan-2.7.1.jar:\
$CP/molgenis/lib/xercesImpl.jar:\
$CP/molgenis/lib/xmlbeans-2.4.0.jar:\
$CP/molgenis/lib/xml-resolver-1.2.jar:\
$CP/molgenis/lib/xmlrpc-client-3.1.3.jar:\
$CP/molgenis/lib/xmlrpc-common-3.1.3.jar:\
$CP/molgenis/lib/XmlSchema-1.4.7.jar:\
$CP/molgenis/lib/xmlsec-1.4.3.jar:\
$CP/molgenis/lib/xmltask.jar:\
$CP/molgenis/lib/apache-poi-3.8.2/poi-3.8-20120326.jar:\
$CP/molgenis/lib/apache-poi-3.8.2/poi-excelant-3.8-20120326.jar:\
$CP/molgenis/lib/apache-poi-3.8.2/poi-ooxml-3.8-20120326.jar:\
$CP/molgenis/lib/apache-poi-3.8.2/poi-ooxml-schemas-3.8-20120326.jar:\
$CP/molgenis/lib/apache-poi-3.8.2/poi-scratchpad-3.8-20120326.jar:\
$CP/molgenis/lib/cobertura/asm.jar:\
$CP/molgenis/lib/cobertura/asm-tree.jar:\
$CP/molgenis/lib/cobertura/cobertura.jar:\
$CP/molgenis/lib/cobertura/log4j.jar:\
$CP/molgenis/lib/cobertura/oro.jar:\
$CP/molgenis/lib/hibernate/antlr-2.7.6.jar:\
$CP/molgenis/lib/hibernate/commons-collections-3.1.jar:\
$CP/molgenis/lib/hibernate/dom4j-1.6.1.jar:\
$CP/molgenis/lib/hibernate/hibernate3.jar:\
$CP/molgenis/lib/hibernate/hibernate-jpa-2.0-api-1.0.0.Final.jar:\
$CP/molgenis/lib/hibernate/hibernate-search-3.4.1.Final.jar:\
$CP/molgenis/lib/hibernate/javassist-3.12.0.GA.jar:\
$CP/molgenis/lib/hibernate/jta-1.1.jar:\
$CP/molgenis/lib/hibernate/c3p0-0.9.1.jar:\
$CP/molgenis/lib/hibernate/slf4j-api-1.6.1.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/hibernate-jpa-2.0-api-1.0.0.Final.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/hibernate-validator-4.1.0.Final.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/log4j-1.2.14.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/slf4j-api-1.5.6.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/slf4j-log4j12-1.5.6.jar:\
$CP/molgenis/lib/hibernate-validator-4.1.0.Final/validation-api-1.0.0.GA.jar \
org.molgenis.compute.commandline.ComputeCommandLine \
-inputdir=$workdir/helloWorld \
-outputdir=$generatedScriptsDir