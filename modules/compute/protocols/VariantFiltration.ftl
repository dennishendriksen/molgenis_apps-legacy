#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=45:00:00 mem=10
inputs "${snpsgenomicannotatedvcf}"
inputs "${indelsmaskbed}"
inputs "${indexfile}"
inputs "${baitintervals}"
inputs "${dbsnprod}"
alloutputsexist "${snpsgenomicannotatedfilteredvcf}"

rm ${snpsgenomicannotatedvcf}.idx

##
# http://www.broadinstitute.org/gsa/wiki/index.php/Best_Practice_Variant_Detection_with_the_GATK_v2
##
# HARD_TO_VALIDATE: more than 10% (or more than 3) of the reads have mapping quality 0
# MQ0 >= 4 && ((MQ0 / (1.0 * DP)) > 0.1)
##
# "Note that For exomes, a straight DP filter shouldn't be used because the
# relationship between misalignments and depth isn't clear for capture data.
# Note that as of Dec. 2010 we no longer recommend using AB to filter, as (1)
# it's not generated by the UG by default any more and (2) QD is a better,
# and largely equivalent, filter."
# QUAL < 30.0 || QD < 5.0 || HRun > 5 || SB > -0.10
# The UG itself: the confidence score and emission thresholds (-stand_call_conf
# and -stand_emit_conf, both Phredscores) are chosen based on the (expected)
# overall coverage. Anything below the emission threshold is not called,
# anything between the two thresholds is flagged LowQual.
# To discuss: a straight DP filter, set to 5 or 10? Or higher? Why, why not?
# For now, I'll include an expression to mark SNPs with a depth of 10 or higher
# with "DEPTH_10".
##

java -jar -Xmx10g ${genomeAnalysisTKjar} \
-l INFO \
-T VariantFiltration \
-B:variant,VCF ${snpsgenomicannotatedvcf} \
-B:mask,Bed ${indelsmaskbed} \
-R ${indexfile} \
-L ${baitintervals} \
-D ${dbsnprod} \
-o ${snpsgenomicannotatedfilteredvcf} \
--maskName InDel \
--clusterWindowSize 10 \
--filterExpression "QUAL < 30.0 || QD < 5.0 || HRun > 5 || SB > -0.10" \
--filterName GATK_EXOME_standard \
--filterExpression "MQ0 >= 4 && ((MQ0 / (1.0 * DP)) > 0.1)" \
--filterName "HARD_TO_VALIDATE" \
--filterExpression "DP > 10" \
--filterName "DEPTH_10"
<@end />