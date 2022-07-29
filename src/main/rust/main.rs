#![no_std]
#![no_main]

mod panic_handler;

use core::ptr::write_volatile;

static HELLO: &[u8] = b"Hello World! This is a message from the Rust!";

#[no_mangle]
pub extern "C" fn _start() -> ! {
    let vga_buffer = 0xb8000 as *mut u8;
    let mut color: u8 = 1;
    let mut ptr: isize = 0;
    unsafe{
        write_volatile(vga_buffer.offset(2), 'a' as u8);
    }
    for _ in 1..=1 {
        for (_, &byte) in HELLO.clone().iter().enumerate() {
            unsafe {
                write_volatile(vga_buffer.offset(ptr), byte);
                write_volatile(vga_buffer.offset(ptr + 1), color);
            }
            color += 1;
            if color == 255 {
                color = 1
            }
            ptr += 2;
        }
    }
    unsafe{
        write_volatile(vga_buffer.offset(4), 'a' as u8);
    }
    loop {}
}
