import json

class Book:
    '''Minecraft PI Book description. Can be sent to Minecraft.addBookToChest(x,y,z,book)

    Book maintains a dictionary which describes the book and then converts it to a 
    json string when sent to the minecraft server. 
    Create a book from a dictionary 
    
    b = mcpi.book.Book({"title":"Python", "author":"RaspberryJuice"})
    
    or from a JSON string
    
    b = mcpi.book.Book.fromJson('{"title":"Python", "author":"RaspberryJuice"}')
    
    Pages can be added at creation time or added later one page at a time from a string
    
    b.addPageOfText("Some text")
    
    or from an array of dictionaries
    
    b.addPage('[{"text":"Some text"}]')
    
    Display parameters can be set at creation time or later from a dictionary
    
    b.setDisplay({"Name":"My display name"})
    
    @author: Tim Cummings https://www.triptera.com.au/wordpress/'''

    def __init__(self, book):
        """creates a book from dictionary. All keys in dictionary are optional but values should be correct type
        
        Example:
        b = mcpi.book.Book({"title":"Python", "author":"RaspberryJuice", "pages":[]})"""
        self.book = book

    def __eq__(self, other):
        return ((self.book) == (other.book))

    def __ne__(self, other):
        return ((self.book) != (other.book))

    def __lt__(self, other):
        return ((self.book) < (other.book))

    def __le__(self, other):
        return ((self.book) <= (other.book))

    def __gt__(self, other):
        return ((self.book) > (other.book))

    def __ge__(self, other):
        return ((self.book) >= (other.book))

    def __iter__(self):
        '''Allows a Book to be used in flatten()'''
        return iter((json.dumps(self.book),))

    def __repr__(self):
        return "Book.fromJson('{}')".format(json.dumps(self.book))
        
    def __str__(self):
        return json.dumps(self.book)

    @staticmethod
    def fromJson(jsonstr):
        """creates book from JSON string. All attributes are optional but should be correct type
        
        Example:
        b = mcpi.book.Book.fromJson('{"title":"Json book","author":"RaspberryJuice","generation":0,"pages":[]}')"""
        return Book(json.loads(jsonstr))

    def addPageOfText(self,text):
        """adds a new page of plain unformatted text to book
        
        New page is added after all existing pages
        Example:
        book.addPageOfText("My text on the page.\n\nNewlines can be added. Can't have formatting (color, bold, italic. etc) or interactivity (clicks or hovers)")"""
        self.addPage([{"text":text}])
    
    def addPage(self, page):
        """adds a new page using an array of dictionaries describing all the text on the new page
        
        New page is added after all existing pages
        Example:
        addPage([
            {"text":'New "(page)"\nThis text is black ', "color":"reset"},
            {"text":"and this text is red, and bold.\n\n", "color":"red", "bold":True},
            {"text":"Hover or click the following\n"},
            {"text":"\nRunning a command\n", "underlined":True, "color":"blue",
                "hoverEvent":{"action":"show_text","value":"runs command to set daytime"},
                "clickEvent":{"action":"run_command", "value":"/time set day"}},
            {"text":"\nOpening a URL\n", "underlined":True, "color":"blue", 
                "hoverEvent":{"action":"show_text","value":"opens url to RaspberryJuice"},
                "clickEvent":{"action":"open_url","value":"https://github.com/zhuowei/RaspberryJuice"}},
            {"text":"\nGoing to a page", "underlined":True, "color":"blue",
                "hoverEvent":{"action":"show_text","value":"goes to page 1"},
                "clickEvent":{"action":"change_page","value":1}}
            ])"""
        if 'pages' not in self.book:
            self.book['pages'] = []
        self.book['pages'].append(page)
    
    def setDisplay(self, display):
        """sets display name and lore and any other display parameters from a dictionary
        
        Replaces any previously set display
        Example:
        b.setDisplay({"Name":"My display string", "Lore":["An array of strings","describing lore"]})"""
        self.book['display'] = display
