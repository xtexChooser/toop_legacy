#![no_std]
#![no_main]

mod panic_handler;

static HELLO: &[u8] = b"Hello World! This is a message from Rust!";

#[no_mangle]
pub extern "C" fn _start() -> ! {
    let vga_buffer = 0xb8000 as *mut u8;
    let mut color: u8 = 1;
    for (i, &byte) in HELLO.iter().enumerate() {
        unsafe {
            *vga_buffer.offset(i as isize * 2) = byte;
            *vga_buffer.offset(i as isize * 2 + 1) = color;
        }
        color += 1;
        if color == 16 {
            color = 1
        }
    }

    loop {
    }
}
