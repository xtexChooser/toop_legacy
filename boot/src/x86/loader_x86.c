static inline void *find_concated_kernel(int *end_search) {
  int *addr = (int *)0x8400;
  while (addr < end_search) {
    if (*addr == 0x464c457f) {
      __asm__("nop"); // avoid found self
      if (*((char *)addr + 4) == 1) {
        return (void *)addr;
      }
    }
    addr = (int *)(((int)addr) + 4);
  }
  while (1)
    ;
}
