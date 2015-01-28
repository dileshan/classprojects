################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../bgpmon_457client/bgpmonclient.c 

OBJS += \
./bgpmon_457client/bgpmonclient.o 

C_DEPS += \
./bgpmon_457client/bgpmonclient.d 


# Each subdirectory must supply rules for building sources it contributes
bgpmon_457client/%.o: ../bgpmon_457client/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


