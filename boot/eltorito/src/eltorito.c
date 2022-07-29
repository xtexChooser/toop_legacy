void eltorito_entrypoint_c();

#include "../../src/loader.c"
#include "../../src/x86/loader_x86.c"

void eltorito_entrypoint_c() {
  int boot_file_size = *((int *)0x7c10);
  void *end_boot_file = (void *)(0x7c00 + boot_file_size);
  void *kernel = find_concated_kernel((int *)end_boot_file);
  load_kernel(kernel, end_boot_file - kernel);
  while (1)
    ;
}
