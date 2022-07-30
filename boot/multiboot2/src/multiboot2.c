#include "multiboot2.h"
#include "../../src/loader.c"

void multiboot_entry_c(unsigned long magic, unsigned long *info_tags) {
  if (((int)info_tags) & 7) {
    // @TODO: error: unaligned MB info
  }
  struct boot_info boot_info;
  boot_info.mem_reserved_map_size = 0;
  struct boot_reserved_mmap *mem_reserved_map =
      (struct boot_reserved_mmap *)0x00001200;
  boot_info.mem_reserved_map = mem_reserved_map;

  struct multiboot_tag *tag = (struct multiboot_tag *)info_tags + 8;
  while (tag->type != MULTIBOOT_TAG_TYPE_END) {
    switch (tag->type) {
    case MULTIBOOT_TAG_TYPE_MODULE: {
      struct multiboot_tag_module *kernel_module =
          (struct multiboot_tag_module *)tag;
      boot_info.kernel_base = kernel_module->mod_start;
      boot_info.kernel_end = kernel_module->mod_end - kernel_module->mod_start;
      break;
    }
    case MULTIBOOT_TAG_TYPE_MMAP: {
      struct multiboot_tag_mmap *mmap = (struct multiboot_tag_mmap *)tag;
      multiboot_memory_map_t *mmap_entry =
          (multiboot_memory_map_t *)mmap->entries;
      while ((void *)mmap_entry < (((void *)mmap) + mmap->size)) {
        if (mmap_entry->type != MULTIBOOT_MEMORY_AVAILABLE) {
          boot_info.mem_reserved_map_size++;
          mem_reserved_map++;
          // @TODO: see QEMU#1131 https://gitlab.com/qemu-project/qemu/-/issues/1131
          volatile unsigned long long addr = mmap_entry->addr;
          mem_reserved_map->base_addr = addr;
          volatile unsigned long long len = mmap_entry->len;
          mem_reserved_map->size = len;
        }
        mmap_entry =
            (multiboot_memory_map_t *)(((void *)mmap_entry) + mmap->entry_size);
      }
      break;
    }
    }
    tag += (tag->size + 7) & ~7;
  }
  load_kernel(&boot_info);
}
