#include "multiboot.h"
#include "../../src/loader.c"

void multiboot_entry_c(unsigned long magic,
                       struct multiboot_info *multiboot_info) {
  if ((multiboot_info->flags & MULTIBOOT_INFO_MEMORY) == 0) {
    while (1)
      ; // @TODO: Missing memory info
  }
  if ((multiboot_info->flags & MULTIBOOT_INFO_MEM_MAP) == 0) {
    while (1)
      ; // @TODO: Missing memory map
  }
  multiboot_module_t *kernel_module =
      (multiboot_module_t *)multiboot_info->mods_addr;

  struct boot_info boot_info;
  boot_info.kernel_base = kernel_module->mod_start;
  boot_info.kernel_end = kernel_module->mod_end;
  boot_info.mem_lower = multiboot_info->mem_lower;
  boot_info.mem_upper = multiboot_info->mem_upper + 1024;

  boot_info.mem_reserved_map_size = 0;

  struct boot_reserved_mmap *mem_reserved_map =
      (struct boot_reserved_mmap *)0x00001200;
  boot_info.mem_reserved_map = mem_reserved_map;

  for (multiboot_memory_map_t *mmap =
           (multiboot_memory_map_t *)multiboot_info->mmap_addr;
       (unsigned long)mmap <
       multiboot_info->mmap_addr + multiboot_info->mmap_length;
       mmap = (multiboot_memory_map_t *)(((void *)mmap) + mmap->size +
                                         sizeof(mmap->size))) {
    if (mmap->type != MULTIBOOT_MEMORY_AVAILABLE) {
      boot_info.mem_reserved_map_size++;
      mem_reserved_map++;
      // @TODO: see QEMU#1131 https://gitlab.com/qemu-project/qemu/-/issues/1131
      volatile unsigned long long addr = mmap->addr;
      mem_reserved_map->base_addr = addr;
      volatile unsigned long long len = mmap->len;
      mem_reserved_map->size = len;
    };
  }
  load_kernel(&boot_info);
}
