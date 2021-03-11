
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Shopping extends JFrame implements ActionListener,FocusListener{

    
    private JButton btnCreate, btnGood; //buttons
    private JTextField txtAgents,txtItems,txtWaitingTime; //textfields
    private JPanel pnlNorth, pnlCenter; //panels
    
    private int numberOfGoods, numberOfAgents, maxWaitingTime; //variables
    private Good g;//Good class for the agents
    private ArrayList<Agent> agentsList; //Agents list for all the agents the user gives
    
    public Shopping() {
        
        setLayout(new BorderLayout()); //Layout
        setSize(1300, 750); //Size
        setTitle("Shopping"); //Name of the GUI
        setResizable(false); //It cannot be resizeable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pnlNorth = new JPanel();
        pnlNorth.setLayout(new FlowLayout());
        pnlNorth.setSize(800, 50);
        //Java Panel's features

        txtItems = new JTextField();
        txtItems.setSize(155, 35);
        txtItems.setText("Number of Goods");
        pnlNorth.add(txtItems);
        //Textfield features for goods

        txtAgents = new JTextField();
        txtAgents.setSize(155, 35);
        txtAgents.setText("Number of Agents");
        pnlNorth.add(txtAgents);
        //Textfield features for agents

        txtWaitingTime = new JTextField();
        txtWaitingTime.setSize(155, 35);
        txtWaitingTime.setText("Max Waiting Time");
        pnlNorth.add(txtWaitingTime);
        //Textfield features for time

        btnCreate = new JButton();
        btnCreate.setSize(125, 25);
        btnCreate.setText("Create");
        pnlNorth.add(btnCreate);
        //Button features for create

        btnGood = new JButton();
        btnGood.setSize(125, 25);
        btnGood.setText("Good");
        pnlNorth.add(btnGood);
        //Button features for Good

        txtItems.addFocusListener(this);
        txtAgents.addFocusListener(this);
        txtWaitingTime.addFocusListener(this);
        btnGood.addActionListener(this);
        btnCreate.addActionListener(this);

        add(pnlNorth, BorderLayout.NORTH);
        setVisible(true);
        //adding all to the listeners
    }
    
    
    
    
    public static void main(String[] args) {
        new Shopping(); //start
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource().equals(btnCreate)) {
            if (pnlCenter != null)
                return;
            //To get values from textfields
            numberOfGoods = Integer.parseInt(txtItems.getText().trim());
            numberOfAgents = Integer.parseInt(txtAgents.getText().trim());
            maxWaitingTime = Integer.parseInt(txtWaitingTime.getText().trim());

            int rows = 10;
            int columns = numberOfGoods / 10;//For having more square

            pnlCenter = new JPanel();
            pnlCenter.setLayout(new GridLayout(rows, columns, 5, 5));//Panel layout with inner space between columns and rows

            g = new Good(numberOfGoods);//Creating Good class with goods parameter

            for (int i = 0; i < g.getLblList().size(); i++)//Adding labels by list in Good class
                pnlCenter.add(g.getLblList().get(i));

            add(pnlCenter, BorderLayout.CENTER);//Adding panel
            validate();
            repaint();

        } 
        else if (e.getSource().equals(btnGood)) {
            agentsList = new ArrayList<>();

            for (int i = 0; i < numberOfAgents; i++) { //Adding agents according to how much the user wants
                agentsList.add(new Agent(g, maxWaitingTime, i));//g will be used by agents, i is  name for agents
                agentsList.get(i).start();//Start
            }

            //This thread will be made sleeping until there are no seats and finally shows messagebox.
            Thread t1 = new Thread(() -> {
                while (g.isAvailable())
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    
                    }
                Message();
            });
            t1.start();
        }
    }
    
    private void Message() {
        //To create and show the message
        StringBuilder m = new StringBuilder();
        for (Agent agent : agentsList) { //foreach loop for to get agents in the list
            m.append("Agent ").append(agent.getName()).append(" bought ").append(agent.getCounter()).append(" goods.\n");
        }
        JOptionPane.showMessageDialog(this, m.toString()); //Convert the message to string
        System.exit(0);//Close with exit
    }

    @Override
    public void focusGained(FocusEvent e) {
        //Focusing to the selected texts 
        if (e.getSource().equals(txtItems))
            txtItems.selectAll();
        else if (e.getSource().equals(txtAgents))
            txtAgents.selectAll();
        else if (e.getSource().equals(txtWaitingTime))
            txtWaitingTime.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        
    }
    
    
    
    class Agent extends Thread {
 
    private Good g;
    private int waitingTime; //Maximum waiting time for make wait agents which books successfully
    private Random randNum;
    private int counter; //Counter for buying goods

    //Constructor
    public Agent(Good g,int waitingTime, int name) { //Constructor
        this.g = g;
        randNum = new Random();
        this.waitingTime = waitingTime;
        counter = 0; //Starting value
        setName(String.valueOf(name + 1)); //To set Agent name
    }



    @Override
    public void run() {
        super.run();
        while (g.isAvailable())//Loop will work while goods are available
            if (g.bookGood())
                try {
                    counter++; //counter increasing for successful shops
                    sleep(randNum.nextInt(waitingTime)); //Sleep for empty time

                } catch (InterruptedException e) {
                }
    }

    public int getCounter() {
        return counter;
    }
    }
    
   public class Good {
    private int numberOfGoods; //Good numbers
    private ArrayList<JLabel> lblList;//Arraylist for goods
    private boolean isAvailable; //For available goods
    private int curr; //To show current available goods

    //Constructor for goods tha takes number of goods as a paramter
    public Good(int numberOfGoods) {
        lblList = new ArrayList<>();
        this.numberOfGoods = numberOfGoods;
        isAvailable = false; //Initial boolean
        createGoods(); //Creating goods
        curr = 0; //Start
    }

    public void createGoods() {
        JLabel defaultLabel; //Default label
        for (int i = 0; i < numberOfGoods; i++) { //Create labels for number of seats
            defaultLabel = new JLabel();
            defaultLabel.setOpaque(true);
            defaultLabel.setBackground(Color.WHITE);
            defaultLabel.setText("Not Booked.");
            lblList.add(defaultLabel); //Adding label to list
        }

        if (lblList.size() > 0) 
            isAvailable = true;
    }

    public synchronized boolean bookGood() { 
        if (curr < lblList.size()) { //To check if there are available goods to buy
            lblList.get(curr).setBackground(Color.GREEN);//Color for good
            lblList.get(curr).setText("Booked by " + Thread.currentThread().getName()); //Changing the name by the agent name of the good
            curr++; //Increasing current for next goods
            return true; //Successful buy
        } else {
            isAvailable = false; //All goods are sold out
            return false;
        }
    }

    //lblList get 
    public ArrayList<JLabel> getLblList() {
        return lblList;
    }

    //isAvailable get
    public boolean isAvailable() {
        return isAvailable;
    }
} 
}
