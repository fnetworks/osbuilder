#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

/* Target Checking */
#if defined(__linux__)
#error "Wrong compiler"
#endif
#if !defined(__i386__)
#error "Wrong compiler target (needs i386)"
#endif

#ifdef __cplusplus
extern "C"
#endif

void kernel_main(void) {
	/* Kernel code here */
}