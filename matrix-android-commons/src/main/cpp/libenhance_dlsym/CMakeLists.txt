cmake_minimum_required(VERSION 3.4.1)
project(libenhance_dlsym CXX)

set(enhance_dlsym_source_dir .)

set(
  enhance_dlsym_source
  ${enhance_dlsym_source_dir}/EnhanceDlsym.cpp
)

add_library(enhance_dlsym STATIC ${enhance_dlsym_source})

find_library(log-lib log)

target_include_directories(
  enhance_dlsym
  PUBLIC ${enhance_dlsym_source_dir}
)

target_link_libraries(
  enhance_dlsym
  PUBLIC ${log-lib}
)