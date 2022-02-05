package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class SteganographyService {
	
	private static final String DIRECTORY = System.getProperty("user.dir") + File.separator + "src" + File.separator + "util" + File.separator + "pictures" + File.separator;
	private static final String FILENAME = "slika";
	private static final String EXTENSION = ".bmp";
	private static final String KEY_DATA_FILENAME = "ReadME.txt";
	
	public static final int NUMBER_OF_QUESTIONS = 20;
	public static final int NUMBER_OF_SESSION_QUESTIONS = 5;
	private static final Random randGenerator = new Random();
	
	private SteganographyService() { }
	
	private static int skipHeader(String filename) {
		try(FileInputStream stream = new FileInputStream(filename)){
			stream.skip(10);
			int pixelArrayLocation = 0;
			for(int i = 0; i < 4; ++i) {
				pixelArrayLocation = pixelArrayLocation | (stream.read() << (4 * i));
			}
			return pixelArrayLocation;
		} catch(IOException exc) {
			exc.printStackTrace();
			return -1;
		}
	}
	
	private static void encode(String filename, String question) {
		int position = skipHeader(filename);
		int readByte = 0;
		
		try(RandomAccessFile stream = new RandomAccessFile(filename, "rw")){
			stream.seek(position);
			for(int i = 0; i < 32; ++i) {
				readByte = stream.read();
				stream.seek(position);
				stream.write(readByte & 0b11111110);
				++position;
			}
			
			question += (char)0;
			int questionByte, questionBit, newByte;
			
			for(char character : question.toCharArray()) {
				questionByte = (int)character;
				for(int i = 0; i < 8; ++i) {
					readByte = stream.read();
					questionBit = (questionByte >> i) & 1;
					newByte = (readByte & 0b11111110) | questionBit;
					stream.seek(position);
					stream.write(newByte);
					++position;
				}
			}
		} catch(IOException exc) {
			exc.printStackTrace();
			return;
		}
	}
	
	private static String decode(String filename) {
		int position = skipHeader(filename);
		try(FileInputStream stream = new FileInputStream(filename)){
			stream.skip(position);
			for(int i = 0; i < 32; ++i) {
				if((stream.read() & 1) != 0) {
					return null;
				}
			}
			
			String result = "";
			int character;
			while(true) {
				character = 0;
				for(int i = 0; i < 8; ++i) {
					character |= ((stream.read() & 1) << i);
				}
				
				if(character == 0) {
					break;
				}
				result += (char)character;
			}
			
			return result;
		} catch(IOException exc) {
			exc.printStackTrace();
			return "IOException : " + exc.getMessage();
		}
	}
	
	private static String questionToString(String question, String answer, String answerList) {
		String result = question + "#";
		result += answer.toLowerCase();
		
		if(answerList != null) {
			result += "#" + answerList;
		}
		
		return result;
	}
	
	private static void imprintQuestions() {
		List<String> questions = new ArrayList<>();

        // 1. question
        questions.add(questionToString("Koja od ovih drzava na svojoj zastavi nema bijelu boju?",
                                        "rumunija",
                                        "Egipat,Danska,Rumunija,Filipini"));
        // 2. question
        questions.add(questionToString("Kako nazivamo strah od otvorenog prostora, javnih mjesta i mnostva ljudi?",
                                        "agorafobija",
                                        null));
        // 3. question
        questions.add(questionToString("Ko je autor djela 'Demijan'?",
                                        "herman hese",
                                        "Mark Tven,Herman Hese,Ernest Hemingvej,Fredrik Bakman"));
        
        // 4. question
        questions.add(questionToString("Kako se zvao rimski vrhovni bog?",
                                        "jupiter",
                                        null));
        // 5. question
        questions.add(questionToString("Vladavina vecine je?",
                                        "demokratija",
                                        "Monarhija,Oligarhija,Demokratija,Anarhija"));
        // 6. question
        questions.add(questionToString("Kako se naziva naucna tvrdnja koja se dokazuje?",
                                        "hipoteza",
                                        null));
        // 7. question
        questions.add(questionToString("Simbol besmrtnosti je?",
                                        "feniks",
                                        "Orao,Sova,Arheopteriks,Feniks"));
        // 8. question
        questions.add(questionToString("Kako se naziva hemijski simbol cija je oznaka 'Au'?",
                                        "zlato",
                                        null));
        // 9. question
        questions.add(questionToString("Koliko ukupno dirki ima klavir?",
                                        "88",
                                        "68,72,88,89"));
        // 10. question
        questions.add(questionToString("Koji filozof je poznat po izreci 'Scio me nihil scire'?",
                                        "sokrat",
                                        null));
        // 11. question
        questions.add(questionToString("Prema legendi, Gordijev cvor je odvezao : ",
                                        "aleksandar makedonski",
                                        "Platon,Filip II Makedonski,Ahil,Aleksandar Makedonski"));
        // 12. question
        questions.add(questionToString("Kako se naziva pismo koje su sastavili Cirilo i Metodije?",
                                        "glagoljica",
                                        null));
        // 13. question
        questions.add(questionToString("Koliko ima umjetnosti?",
                                        "7",
                                        "5,6,7,8"));
        // 14. question
        questions.add(questionToString("Koji je 6. padez u srpskom jeziku?",
                                        "instrumental",
                                        null));
        // 15. question
        questions.add(questionToString("Gdje je rodjen Jovan Ducic?",
                                        "trebinje",
                                        "Beograd,Zagreb,Sarajevo,Trebinje"));
        // 16. question
        questions.add(questionToString("Kako se, u muzici, naziva raspon od 7 tonova?",
                                        "septima",
                                        null));
        // 17. question
        questions.add(questionToString("Ko je komponovao 'Labudovo jezero'?",
                                        "cajkovski",
                                        "Cajkovski,Betoven,Rosini,Mocart"));
        // 18. question
        questions.add(questionToString("Ko pjeva pjesmu 'Bato'?",
                                        "kaliopi",
                                        null));
        // 19. question
        questions.add(questionToString("Hijeroglifsko pismo je nastalo u :",
                                        "egiptu",
                                        "Mesopotamiji,Egiptu,Vavilonu,Sumeru"));
        // 20. question
        questions.add(questionToString("Koji je glavni grad Brazila?",
                                        "brazilija",
                                        null));
        
        try {
        	List<String> encryptedData = CryptographyService.encryptDataList(questions, DIRECTORY + KEY_DATA_FILENAME);
        	int i = 1;
        	for(String question : encryptedData) {
        		encode(DIRECTORY + FILENAME + i + EXTENSION, question);
        		++i;
        	}
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
	}
	
	public static void initialize() {
		imprintQuestions();
	}
	
	public static List<Question> getQuestions(){
		List<Question> sessionQuestions = new ArrayList<>();
		
		while(sessionQuestions.size() < NUMBER_OF_SESSION_QUESTIONS) {
			int id = randGenerator.nextInt(NUMBER_OF_QUESTIONS) + 1;
			String encryptedData = decode(DIRECTORY + FILENAME + id + EXTENSION);
			
			if(encryptedData == null) {
				return null;
			}
			
			try {
				String result = CryptographyService.decryptData(encryptedData.getBytes(), DIRECTORY + KEY_DATA_FILENAME);
				String[] questionParts = result.split("#");
				if(questionParts.length < 2) {
					continue;
				}
			
				String[] answerList = null;
				if(questionParts.length > 2) {
					answerList = questionParts[2].split(",");
				}
			
				Question newQuestion = new Question(questionParts[0], questionParts[1], answerList);
				if(!sessionQuestions.contains(newQuestion)) {
					sessionQuestions.add(new Question(questionParts[0], questionParts[1], answerList));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return sessionQuestions;
	}
}
