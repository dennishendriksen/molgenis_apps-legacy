#MOLGENIS walltime=00:45:00

inputs "${filehandleRemoveSampleString}"

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --indep-pairwise ${snpWindowN} ${snpIntervalM} ${rSquaredThreshold} --out ${filehandlePairwiseString}
 