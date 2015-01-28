################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../bgpmon_457client/bgpmonclient.cpp 

OBJS += \
./bgpmon_457client/bgpmonclient.o 

CPP_DEPS += \
./bgpmon_457client/bgpmonclient.d 


# Each subdirectory must supply rules for building sources it contributes
bgpmon_457client/%.o: ../bgpmon_457client/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


