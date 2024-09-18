use rand::Rng;
use std::net::UdpSocket;
use std::time::SystemTime;
use std::env;
use std::process::Command;

fn get_random_rastreio_data(mac_address: &str) -> String {
    let mut rng = rand::thread_rng();
    let latitude: f64 = rng.gen_range(-90.0..90.0);
    let longitude: f64 = rng.gen_range(-180.0..180.0);
    let altitude: i32 = rng.gen_range(0..1000);
    let velocidade: i32 = rng.gen_range(0..120);
    let direcoes = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"];
    let direcao = direcoes[rng.gen_range(0..direcoes.len())];
    let status_veiculo = "Normal";
    let timestamp = SystemTime::now().duration_since(SystemTime::UNIX_EPOCH).unwrap().as_secs();

    format!("{},{},{},{},{},{},{},{}", 
        mac_address, latitude, longitude, altitude, velocidade, direcao, status_veiculo, timestamp)
}

fn send_udp_messages(mac_address: &str, udp_ip: &str, udp_port: u16) {
    let addr = format!("{}:{}", udp_ip, udp_port);
    let socket = UdpSocket::bind("0.0.0.0:0").expect("Couldn't bind to address");

    loop {
        let message = get_random_rastreio_data(mac_address);
        let _ = socket.send_to(message.as_bytes(), &addr);
    }
}

fn main() {
    let args: Vec<String> = env::args().collect();
    
    if args.len() == 4 && args[1] == "send" {
        // Modo de envio para um processo filho
        let udp_port: u16 = args[2].parse().expect("Invalid port");
        let mac_address = &args[3];
        let udp_ip = "127.0.0.1";
        send_udp_messages(mac_address, udp_ip, udp_port);
    } else if args.len() == 2 {
        // Modo principal: inicia processos filhos
        let udp_port: u16 = args[1].parse().expect("Provide a valid port as argument");

        let caminhoes = vec![
            "00:1A:2B:3C:4D:5E", "AA:BB:CC:DD:EE:FF", "11:22:33:44:55:66", "AA:11:BB:22:CC:33",
            "DD:EE:FF:00:11:22", "33:44:55:66:77:88", "99:88:77:66:55:44", "AB:CD:EF:01:23:45",
            "67:89:AB:CD:EF:01", "23:45:67:89:AB:CD",
        ];

        for caminhao in caminhoes {
            Command::new(env::current_exe().unwrap())
                .arg("send")
                .arg(udp_port.to_string())
                .arg(caminhao)
                .spawn()
                .expect("Failed to spawn process");
        }

        println!("Todos os processos de envio foram iniciados. Pressione Ctrl+C para encerrar.");
        std::thread::park(); // Espera indefinidamente
    } else {
        eprintln!("Usage: {} <udp_port>", args[0]);
        std::process::exit(1);
    }
}

