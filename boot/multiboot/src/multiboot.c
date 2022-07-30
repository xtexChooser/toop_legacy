#include "multiboot.h"
#include "../../src/loader.c"

void multiboot_entry_c(unsigned long magic, struct multiboot_info *boot_info) {
  multiboot_module_t *kernel_module =
      (multiboot_module_t *)boot_info->mods_addr;
  load_kernel((void *)kernel_module->mod_start,
              (kernel_module->mod_end - kernel_module->mod_start));
}
