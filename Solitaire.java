//imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Solitaire extends JFrame {
    
    //instance variables
    private static final int CARD_WIDTH = 73;
    private static final int CARD_HEIGHT = (int)(CARD_WIDTH*1.49137931+.5);
    private static final int TOP_STACKS_X_DIFF = 435;
    private static final int TOP_STACKS_Y_DIFF = 30;
    private static final int WASTE_STACK_X_DIFF = 139;
    private static final int WASTE_STACK_Y_DIFF = 30;
    private static final int MAIN_STACKS_X_DIFF = 30;
    private static final int MAIN_STACKS_Y_DIFF = 40;
        
    private final JLabel newStack;
    private final JLabel wasteStack;
    private final JLabel[] topStacks;
    private final JLayeredPane[] mainStacks;
    
    private JLabel selectedCard;
    private boolean cardSelected;
    
    private Card wasteCard;
    private Deck deckOfCards;
    
    //methods
    /**
     * Creates new form gameScreenGUI
     */
    public Solitaire() {
        //setting up JFrame
        setTitle("Solitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(0,153,0));
        
        //Create the topStacks
        topStacks = new JLabel[4]; //initializing topStacks
        for (int index = 0; index < topStacks.length; index++) { //loop through to create stack for each suit
            topStacks[index] = new JLabel(); //creating new stack
            topStacks[index].setBounds((index*CARD_WIDTH) + (index*10) + TOP_STACKS_X_DIFF, TOP_STACKS_Y_DIFF, CARD_WIDTH, CARD_HEIGHT); //creating the boundary for each top stack
            topStacks[index].setBorder(BorderFactory.createLineBorder(Color.BLACK)); //setting the border for each top stack
            add(topStacks[index]); //adding to GUI
        } //end of for loop
        
        //Create the wasteStack
        wasteStack = new JLabel(); //initializing wasteStack
        wasteStack.setBounds(WASTE_STACK_X_DIFF, WASTE_STACK_Y_DIFF, CARD_WIDTH, CARD_HEIGHT); //creating the boundary for the waste stack
        wasteStack.setBorder(BorderFactory.createLineBorder(Color.BLACK)); //setting the border for the boundary of the waste stack
        add(wasteStack); //adding to GUI
        
        //Create the newStack
        newStack = new JLabel(); //initializing newStack
        newStack.setBounds(WASTE_STACK_X_DIFF - CARD_WIDTH - 36, WASTE_STACK_Y_DIFF, CARD_WIDTH, CARD_HEIGHT); //creating the boundary for the new stack
        newStack.setBorder(BorderFactory.createLineBorder(Color.BLACK)); //setting the border for the boundary of the new stack
        newStack.addMouseListener(new newStackOnClick()); //adding event
        add(newStack); //adding to GUI
        
        //Create the mainStacks
        mainStacks = new JLayeredPane[7]; //initializing mainStacks
        for (int index = 0; index < mainStacks.length; index++) { //loop through to create 7 stacks
            mainStacks[index] = new JLayeredPane(); //creating new stack
            mainStacks[index].setBounds((index*CARD_WIDTH) + (index*36) + MAIN_STACKS_X_DIFF, MAIN_STACKS_Y_DIFF + (CARD_HEIGHT + 10), CARD_WIDTH, CARD_HEIGHT); //creating the boundary for each main stack
            mainStacks[index].setBorder(BorderFactory.createLineBorder(Color.BLACK)); //setting the border for each main stack
            add(mainStacks[index]); //add to GUI
        } //end of for loop

        
        setLayout(null); //changing to null layout
        setSize(800, 600); //defining size of window
        setVisible(true); //displaying window
        startGame(); //starts the game
    } //end of constructor method
    
    /**
     * This method creates the game.
     */
    private void startGame() {
        //creates and shuffles a new deck
        deckOfCards = new Deck();
        deckOfCards.shuffleDeck();

        
        for (int stack = 0; stack < mainStacks.length; stack++) { //loop through the stacks in the mainStacks
            int stackHeight = 0; //stores the height of the stack
            
            for (int cardInStack = 0; cardInStack <= stack; cardInStack++) { //loop through to add the cards in each Stack
                Card placementCard = deckOfCards.drawCard(); //draws card
                placementCard.setIsFaceUp(false);
                //checking if the card is the last card in the stack
                if (stack == cardInStack) {
                    placementCard.setIsFaceUp(true); //card is facing up
                    //sizes the image and sets it to placementCard
                    ImageIcon placementCardFace = new ImageIcon(placementCard.getImagePath());
                    Image placementCardScaled = placementCardFace.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                    JLabel cardLabel = new JLabel(new ImageIcon(placementCardScaled));
                    cardLabel.setBounds(0,cardInStack*22, CARD_WIDTH, CARD_HEIGHT);
                    cardLabel.putClientProperty("card", placementCard);
                    mainStacks[stack].add(cardLabel, Integer.valueOf(cardInStack));
                } //end of if statement
                
                //if statement to check whether the card should be revealed or not
                if (!placementCard.getIsFaceUp()) { //hide cardFront
                    //sizes the image and sets it to placementCard
                    ImageIcon placementCardBack = new ImageIcon(Card.getBackImagePath());
                    Image placementCardScaled = placementCardBack.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                    JLabel cardLabel = new JLabel(new ImageIcon(placementCardScaled));
                    cardLabel.setBounds(0,cardInStack*22, CARD_WIDTH, CARD_HEIGHT);
                    cardLabel.putClientProperty("card", placementCard);
                    mainStacks[stack].add(cardLabel, Integer.valueOf(cardInStack));
                } //end of if statement
                
                stackHeight = CARD_HEIGHT + (22*stack); //increments the stack height with each new card
                
            } //end of for loop
            
            mainStacks[stack].setSize(CARD_WIDTH, stackHeight); //sets the size of the stack so that all cards show
            
        } //end of for loop
        
        MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
            if (cardSelected) {
                    JLabel stackClicked = (JLabel)e.getSource();
                    for (int mainStackIndex = 0; mainStackIndex < mainStacks.length; mainStackIndex++) {
                        JLabel topCardLabel = getTopMStackCardLabel(mainStackIndex);
                        if (stackClicked.equals(topCardLabel)) {
                            if (movableToMainStack(selectedCard, mainStackIndex)) {
                                mainStacks[mainStackIndex].remove(topCardLabel);
                                mainStacks[mainStackIndex].add(selectedCard, Integer.valueOf(mainStacks[mainStackIndex].getComponentCount()));
                                mainStacks[mainStackIndex].moveToFront(selectedCard);
                                mainStacks[mainStackIndex].revalidate();
                                mainStacks[mainStackIndex].repaint();
                                mainStacks[mainStackIndex].add(Solitaire.this.selectedCard);
                                Solitaire.this.selectedCard.setLocation(topCardLabel.getLocation());
                                Solitaire.this.selectedCard = null;
                                cardSelected = false;
                                repaint();
                                revalidate();
                            }
                            break;
                        }
                    }
                } else {
                    selectedCard = (JLabel)e.getSource();
                    System.out.println(selectedCard);
                    cardSelected = true;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            
        }; //end of MouseListener
        
        for (int stack = 0; stack < mainStacks.length; stack++) { //loop through the stacks in the mainStacks            
            for (int cardInStack = 0; cardInStack <= stack; cardInStack++) { //loop through to add the cards in each Stack
                JLabel cardLabel = getTopMStackCardLabel(stack);
                cardLabel.addMouseListener(ml);
                cardLabel.setTransferHandler(new TransferHandler("icon"));
            }
        }
        
        //sizes the image and sets it to newCard
        ImageIcon cardBack = new ImageIcon(Card.getBackImagePath());
        Image cardBackScaled = cardBack.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        newStack.setIcon(new ImageIcon(cardBackScaled));
        
        wasteCard = deckOfCards.drawCard(); //new card from pile to possibly add to mainStacks
        wasteCard.setIsFaceUp(true); //sets card facing up        
        
        //sizes the image and sets it to wasteStack
        ImageIcon wasteCardFace = new ImageIcon(wasteCard.getImagePath());
        Image wasteCardFaceScaled = wasteCardFace.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        wasteStack.addMouseListener(ml);
        wasteStack.setIcon(new ImageIcon(wasteCardFaceScaled));
                        
    } //end of startGame method
    
    /**
     * This method returns the card corresponding to the image on the GUI.
     * @param cardLabel - the image shown on the GUI
     * @return Card - the card with the properties of the image
     */
    private Card getCardFromLabel(JLabel cardLabel) {
        return (Card) cardLabel.getClientProperty("card");
    } //end of getCardFromLabel method
    
    public Card getTopMStackCard(int stackIndex) {
        JLayeredPane mainStack = mainStacks[stackIndex];
        if (mainStack.getComponentCount() > 0) {
            JLabel topCardLabel = (JLabel) mainStack.getComponent(mainStack.getComponentCount() - 1);
            // Assuming the Card object is stored as the client property of the JLabel
            return (Card) topCardLabel.getClientProperty("card");
        }
        return null;
    } //end of getTopMStackCard method
    public JLabel getTopMStackCardLabel(int stackIndex) {
        JLayeredPane mainStack = mainStacks[stackIndex];
        if (mainStack.getComponentCount() > 0) {
            return (JLabel) mainStack.getComponent(0);
        }
        return null;
    } //end of getTopMStackCardLabel method
    
    /**
     * This method checks if the selected card can be moved from one stack to the main stacks.
     * @param card - the card being checked
     * @param stackNumber - the stack which the card is being checked for
     * @return Boolean - true or false depending on whether the card can be moved to the stack or not
     */
    private boolean movableToMainStack(JLabel cardLabel, int stackNumber) {
        Card card = getCardFromLabel(cardLabel);
        Card topCard = getTopMStackCard(stackNumber);
        
        if (topCard == null) {
            return card.getRank() == Rank.KING;
        } else {
            return card.getRank().ordinal() == topCard.getRank().ordinal() - 1 && card.stackable(topCard);
        }
    }
    
    private void newStackClick() {
        if(!deckOfCards.isEmpty()) {
            wasteCard = deckOfCards.drawCard();
            System.out.println(wasteCard);
            wasteCard.setIsFaceUp(true);
            
            ImageIcon wasteCardFace = new ImageIcon(wasteCard.getImagePath());
            Image wasteCardScaled = wasteCardFace.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
            wasteStack.setIcon(new ImageIcon(wasteCardScaled));
        } else {
            resetNewStack();
        } //end of if statement
    } //end of newStackClick method
    
    private void resetNewStack() {
        deckOfCards.resetDrawCardIndex();
        deckOfCards.shuffleDeck();
        wasteCard = deckOfCards.drawCard();
        wasteCard.setIsFaceUp(true);
        
        ImageIcon wasteCardFace = new ImageIcon(wasteCard.getImagePath());
        Image wasteCardScaled = wasteCardFace.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        wasteStack.setIcon(new ImageIcon(wasteCardScaled));
        
        ImageIcon cardBack = new ImageIcon(Card.getBackImagePath());
        Image cardBackScaled = cardBack.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        newStack.setIcon(new ImageIcon(cardBackScaled));
    } //end of resetNewStack method
    
    private class newStackOnClick extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            newStackClick();
        } //end of mouseClicked method
    } //end of newStackOnClick class

    
    public static void main(String[] args) {
        new Solitaire();
    } //end of main method

} //end of Solitaire class