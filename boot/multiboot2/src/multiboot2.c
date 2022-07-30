#include "../../src/loader.c"
#include "multiboot2.h"

void multiboot_entry_c(unsigned long magic, unsigned long *info_tags) {
  if (((int) info_tags) & 7) {
    // @TODO: error: unaligned MB info
  }
  struct multiboot_tag *tag = (struct multiboot_tag *)info_tags + 8;
  struct multiboot_tag_module *kernel_module;
  while (tag->type != MULTIBOOT_TAG_TYPE_END) {
    switch (tag->type) {
    case MULTIBOOT_TAG_TYPE_MODULE:
      kernel_module = (struct multiboot_tag_module *)tag;
      break;
    }
    tag += (tag->size + 7) & ~7;
  }
  load_kernel((void *)kernel_module->mod_start,
              (kernel_module->mod_end - kernel_module->mod_start));
}
