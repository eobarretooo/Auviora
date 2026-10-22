# Apply the pinned Android portability patch without changing the gitlink.
cmake_minimum_required(VERSION 3.22.1)
find_package(Git REQUIRED)
if(NOT DEFINED LLAMA_SRC)
    get_filename_component(LLAMA_SRC "${CMAKE_CURRENT_LIST_DIR}/../vendor/llama.cpp-omni" ABSOLUTE)
endif()
set(patch "${CMAKE_CURRENT_LIST_DIR}/../patches/llama.cpp-omni/0001-portable-expert-cache.patch")
execute_process(COMMAND "${GIT_EXECUTABLE}" -C "${LLAMA_SRC}" rev-parse HEAD
    OUTPUT_VARIABLE revision OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
if(NOT revision STREQUAL "96bbf61710d44421ea54e63f5194f9b17c20d877")
    message(FATAL_ERROR "Native patch expects llama.cpp-omni 96bbf617; review patch before updating the submodule")
endif()
execute_process(COMMAND "${GIT_EXECUTABLE}" -C "${LLAMA_SRC}" apply --reverse --check "${patch}"
    RESULT_VARIABLE already_applied OUTPUT_QUIET ERROR_QUIET)
if(already_applied EQUAL 0)
    message(STATUS "Native portability patch already applied")
    return()
endif()
execute_process(COMMAND "${GIT_EXECUTABLE}" -C "${LLAMA_SRC}" apply --check "${patch}"
    RESULT_VARIABLE can_apply ERROR_VARIABLE details)
if(NOT can_apply EQUAL 0)
    message(FATAL_ERROR "Native patch conflicts with checkout; preserving local changes. ${details}")
endif()
execute_process(COMMAND "${GIT_EXECUTABLE}" -C "${LLAMA_SRC}" apply "${patch}"
    COMMAND_ERROR_IS_FATAL ANY)
message(STATUS "Applied native portability patch (submodule working tree is intentionally modified)")
