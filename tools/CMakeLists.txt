cmake_minimum_required (VERSION 2.8.9)

get_filename_component(ProjectId ${CMAKE_CURRENT_SOURCE_DIR}/.. REALPATH)
get_filename_component(ProjectId ${ProjectId} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(${ProjectId})

if(CMAKE_COMPILER_IS_GNUCC)
# force 32bit build     
set(CMAKE_CXX_FLAGS "-m32 ${CMAKE_CXX_FLAGS}")
set(CMAKE_C_FLAGS   "-m32 ${CMAKE_C_FLAGS}")
endif()

add_subdirectory($ENV{COREVA_SOFTWARE}/mpsoc_com-lib		$ENV{COREVA_SOFTWARE}/mpsoc_com-lib/build)
add_subdirectory($ENV{COREVA_SOFTWARE}/fio_com-lib		$ENV{COREVA_SOFTWARE}/fio_com-lib/build)
add_subdirectory($ENV{COREVA_SOFTWARE}/profiling		$ENV{COREVA_SOFTWARE}/profiling/build)

set(CMAKE_BUILD_TYPE Debug)

if(MSVC)
# pthread sources for windows build
include_directories($ENV{COREVA_SOFTWARE}/tools/pthread/include)
link_directories($ENV{COREVA_SOFTWARE}/tools/pthread/lib/x86)
endif()
find_package (Threads)

include_directories($ENV{COREVA_SOFTWARE}/include)

AUX_SOURCE_DIRECTORY(../coreva/cores/ CURRENT_DIR) 
  
set(SOURCE_LIB
	${CURRENT_DIR}
	../coreva/main_pthread.c
)

find_package (Threads)
 
add_executable(${ProjectId} ${SOURCE_LIB})

if(MSVC)
set(ADDITIONAL_LIBS
)
else(MSVC)
set(ADDITIONAL_LIBS
	m
)
endif(MSVC)

# Additional libraries
#target_link_libraries (${ProjectId} m mpsoc_com-lib streamit-complex-lib streamit-math-lib ${CMAKE_THREAD_LIBS_INIT})
target_link_libraries (${ProjectId} ${ADDITIONAL_LIBS} mpsoc_com-lib fio_com-lib ${CMAKE_THREAD_LIBS_INIT})
