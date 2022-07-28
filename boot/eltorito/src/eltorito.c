#include "../../loader/src/loader.h"

void eltorito_entrypoint_c() {
  char *fb = (char *)0xb8000;
  *fb = 'H';
  *(fb + 2) = 'E';
  *(fb + 4) = 'L';
  *(fb + 6) = 'L';
  *(fb + 8) = 'O';
  *(fb + 10) = 'W';
  while (1);
}
