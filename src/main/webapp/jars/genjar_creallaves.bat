@echo off
@echo Borrando fortimax_itam_creallaves.jar...
del /F fortimax_itam_creallaves.jar
@echo procesando JARS
jar -xf dependences\plugin.jar
jar -xf dependences\bcprov-jdk14-137.jar
rem jar -xf dependences\bcmail-jdk14-137.jar
jar -xf dependences\itext-2.0.4.jar
@echo Creando fortimax_itam_creallaves.jar...
jar -cf fortimax_itam_creallaves.jar -C ..\WEB-INF\classes com\syc\applet\keys -C ..\WEB-INF\classes com\syc\applet\filters
jar -uf fortimax_itam_creallaves.jar meta-inf/pack.properties netscape sun org com
@echo Indexando...
jar -i fortimax_itam_creallaves.jar
@echo Firmando...
rem jarsigner -keystore fortimax-keystore -storepass fortimax -keypass fortimax fortimax_itam_creallaves.jar fortimax
jarsigner -keystore fortimax_itam.keystore -storepass f0r7im4x!17aM -keypass f0r7im4x!17aM fortimax_itam_creallaves.jar fortimax_itam
rem @echo Verificando...
rem jarsigner -verify -verbose fortimax_itam_creallaves.jar
@echo fortimax_itam_creallaves.jar creado
pause