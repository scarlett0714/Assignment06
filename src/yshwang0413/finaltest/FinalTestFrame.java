package yshwang0413.finaltest;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class FinalTestFrame extends JFrame implements ActionListener {
	VolunteerManager yshwang = new VolunteerManager("좋은세상만들기");
	Volunteer v; // 봉사활동 정보생성
	Container frame = this.getContentPane();

	JPanel menu = new JPanel(new GridLayout(3, 0)); // 1,2,3번째 패널담기 : frmae의 NORTH에 위치

	JPanel first = new JPanel(new FlowLayout()); // 첫번째 패널
	JPanel date = new JPanel(); // 날짜, 시간

	JPanel second = new JPanel(new FlowLayout()); // 두번째 패널
	JTextField volName = new JTextField(20); // 봉사활동 이름
	JTextField volTime = new JTextField(10); // 봉사활동 시간
	JTextField volMemberNum = new JTextField(10); // 모집인원 정보
	String status; // 봉사활동 등록가능여부

	JPanel third = new JPanel(new FlowLayout()); // 세번째 패널
	DefaultComboBoxModel<String> cbmodel = new DefaultComboBoxModel<>(); // combobox 동적할당
	JComboBox<String> combo = new JComboBox<>(cbmodel);
	JTextField name = new JTextField(15);

	JPanel four = new JPanel(); // Table : frame의 CENTER에 위치
//테이블 세팅
	String[] colum = { "이름", "성별", "나이", "봉사시간" };
	DefaultTableModel model;
	JTable table;
	Map<String, JTable> tables = new HashMap<>();
	Set<Map.Entry<String, JTable>> set;

	JButton save = new JButton("저장하기"); // frame의 SOUTH에 위치
	Map<String, ArrayList<String>> map = new HashMap<>();
	Set<Map.Entry<String, ArrayList<String>>> setList = map.entrySet();
	boolean flag = false; // 스레드 종료

	public FinalTestFrame() {
		this("202110547 황윤선");
	}

	public FinalTestFrame(String title) {
		super(title);
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
		this.setVisible(true);
	}

	public void init() {

		// 첫번째 패널 세팅
		MyLabel label = new MyLabel();
		date.add(label);
		first.add(date);

		JButton stop = new JButton("stop");

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				flag = true;

			}

		});

		first.add(stop);
		first.setBackground(Color.BLACK);
		menu.add(first);

		// 두번째 패널 세팅
		JButton addbtn = new JButton("추가");
		// 추가 버튼을 누르면
		addbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (volName.getText() != null && volTime.getText() != null && volMemberNum.getText() != null) {
					v = new Volunteer(volName.getText(), Integer.parseInt(volTime.getText()),
							Integer.parseInt(volMemberNum.getText()));
					// Integer.parseInt(String) : 문자열->정수로 변환
					status = yshwang.addVolunteer(v);
					if (status.equals("중복 등록 불가")) {
						JOptionPane.showMessageDialog(null, "중복 등록 불가", "MESSAGE", JOptionPane.DEFAULT_OPTION);

					} else {
						JOptionPane.showMessageDialog(null, "등록완료", "MESSAGE", JOptionPane.DEFAULT_OPTION);

					}
				} else {
					JOptionPane.showMessageDialog(null, "봉사활동 정보를 모두 입력해주세요", "WARNING", JOptionPane.WARNING_MESSAGE);
				}

				if (status.equals("등록완료")) {
					cbmodel.addElement(volName.getText());

					// Map형태로 Table 저장
					makeTable();
					
					// data저장할 Map형태 만들기
					makeDataMap();

				}

			}

			private void makeDataMap() {
				Iterator<Entry<String, ArrayList<String>>> it3 = setList.iterator();
				while (it3.hasNext()) {
					Entry<String, ArrayList<String>> entry = it3.next();
					String key = entry.getKey();
					if (volName.getText().equals(key)) {
						return;
					}
				}

				map.put(volName.getText(), new ArrayList<String>());

			}

			public void makeTable() {
				set = tables.entrySet();
				Iterator<Map.Entry<String, JTable>> it3 = set.iterator();
				while (it3.hasNext()) {
					Map.Entry<String, JTable> entry = it3.next();
					String key = entry.getKey();
					if (volName.getText().equals(key)) {
						return;
					}
				}

				// 모델생성
				model = new DefaultTableModel(colum, 0); // (컬럼, row개수)
				// 위의 모델로 테이블 생성
				table = new JTable(model);
				tables.put(volName.getText(), table);

			}

		});

		second.add(volName);
		second.add(volTime);
		second.add(volMemberNum);
		second.add(addbtn);
		second.setBackground(Color.YELLOW);
		menu.add(second);

		name.addActionListener(this);

		frame.add(four, BorderLayout.CENTER);

		// 세번째 패널 세팅
		third.add(combo);
		cbmodel.addElement("선택하세요");

		third.add(name);

		menu.add(third);
		third.setBackground(Color.BLACK);
		frame.add(menu, BorderLayout.NORTH);

		// 저장하기 버튼 추가
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fileWriter();

			}

			private void fileWriter() {
				String filePath = "C:\\Users\\tptty\\Desktop\\학교자료\\1-2\\JAVA 프로그래밍\\과제\\"
						+ (String) combo.getSelectedItem() + ".txt";

				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
					PrintWriter writer = new PrintWriter(bw, true);
					writer.write(map.get((String) combo.getSelectedItem()).toString());
					writer.flush();
					writer.close();

				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "파일에 단어를 추가할 수 없습니다.", "WARNING", JOptionPane.ERROR_MESSAGE);
				}

			}

		});

		combo.addActionListener(this);
		frame.add(save, BorderLayout.SOUTH);

	}

	@SuppressWarnings("serial")
	class MyLabel extends JLabel implements Runnable {
		// 기존 클래스와는 별도로 시간을 갱신하는 Label

		SimpleDateFormat time = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
		// 위와 같은 형태로 데이터 출력

		MyLabel() {
			this.setFont(new Font("궁서체", Font.BOLD, 24));
			this.setOpaque(true);
			this.setBackground(Color.GREEN);
			this.setHorizontalAlignment(CENTER); // 글자가 들어오면 CENTER에 정렬
			Thread th = new Thread(this);
			th.start();
		}

		@Override
		public void run() {
			while (true) {

				this.setText(time.format(Calendar.getInstance().getTime())); // 시간정보가져온 것을 위의 데이터포맷에 맞추기
				try {
					Thread.sleep(1000);
					if (flag == true)
						return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTable t = findTable((String) combo.getSelectedItem());
		frame.add(new JScrollPane(t), BorderLayout.CENTER); // 중앙에 table배치

		if (e.getSource() == name) {
			Volunteer vol = yshwang.findVolunteer((String) combo.getSelectedItem());
			if (t != null && t.getModel().getRowCount() < vol.Number) {
				// 회원 등록

				Member m = yshwang.getMember(name.getText());
				if (m != null) {
					vol.join(m);

					String gender;
					if (m.gender == 1) {
						gender = "남";
					} else {
						gender = "여";
					}

					String[] data = { m.name, gender, Integer.toString(m.age), Integer.toString(m.totalDonationTime) };
					String str = "";
					for (int i = 0; i < data.length - 1; i++) {
						str += data[i] + ", ";
					}
					str += data[data.length - 1];

					Iterator<Entry<String, ArrayList<String>>> it3 = setList.iterator();
					while (it3.hasNext()) {
						Entry<String, ArrayList<String>> entry = it3.next();
						String key = entry.getKey();
						if (vol.volName.equals(key)) {
							map.get(key).add(str);
							break;
						}
					}

					// 테이블에 추가
					((DefaultTableModel) t.getModel()).addRow(data); // 행으로 추가됨
					JOptionPane.showMessageDialog(null,
							name.getText() + "님 " + (String) combo.getSelectedItem() + "에 참여하셨습니다.", "MESSAGE",
							JOptionPane.DEFAULT_OPTION);
				} else {
					JOptionPane.showMessageDialog(null,
							name.getText() + "님 " + (String) combo.getSelectedItem() + "회원 가입 먼저하세요.", "MESSAGE",
							JOptionPane.DEFAULT_OPTION);
				}

			} else {
				JOptionPane.showMessageDialog(null, "모집인원을 초과하였습니다.", "WARNING", JOptionPane.WARNING_MESSAGE);
			}
		}

	}

	private JTable findTable(String findVolName) {
		Iterator<Map.Entry<String, JTable>> it3 = set.iterator();
		while (it3.hasNext()) {
			Map.Entry<String, JTable> entry = it3.next();
			String key = entry.getKey();
			if (findVolName.equals(key)) {
				return entry.getValue();
			}
		}
		return null;

	}

}

