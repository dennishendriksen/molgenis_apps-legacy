





${gtoolBin} -G --g ${imputationResult}/chr_${chr} --s ${preparedStudyDir}/chr${chr}.sample --ped ${imputationResult}/~chr_${chr}.ped --map ${imputationResult}/~chr_${chr}.map



if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${imputationResult}/~chr_${chr}.ped ${imputationResult}/chr_${chr}.ped
	mv ${imputationResult}/~chr_${chr}.map ${imputationResult}/chr_${chr}.map

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi